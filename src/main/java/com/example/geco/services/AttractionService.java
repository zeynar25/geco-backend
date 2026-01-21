package com.example.geco.services;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import com.example.geco.utils.ImageUtils;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.geco.domains.Attraction;
import com.example.geco.domains.AuditLog.LogAction;
import com.example.geco.dto.AttractionRequest;
import com.example.geco.dto.AttractionResponse;
import com.example.geco.repositories.AttractionRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class AttractionService extends BaseService{
	@Autowired
	private StorageService storageService;

	@Value("${app.storage.bucket.attraction_images}")
	private String imagesBucket;

	@Value("${app.storage.bucket.attraction_models}")
	private String modelsBucket;

	@Value("${app.tools.gltfpack:}")
	private String gltfpackPath;

	private static final Logger log = LoggerFactory.getLogger(AttractionService.class);
	
	@Autowired
	AttractionRepository attractionRepository;

	private byte[] compressImage(MultipartFile file, float quality, int maxWidth, int maxHeight) throws IOException {
		return ImageUtils.compressImage(file, quality, maxWidth, maxHeight);
	}

	@Transactional(readOnly = true)
	public long getAttractionsNumber() {
		return attractionRepository.count();
	}
	
	public Attraction createAttractionCopy(Attraction a) {
		return Attraction.builder()
				.attractionId(a.getAttractionId())
				.name(a.getName())
				.description(a.getDescription())
				.funFact(a.getFunFact())
				.photo2dUrl(a.getPhoto2dUrl())
				.glbUrl(a.getGlbUrl())
				.isActive(a.isActive())
			    .build();
	}
	
	private AttractionResponse toResponse(Attraction a) {
		String photo = a.getPhoto2dUrl();
		if (photo != null && !photo.startsWith("http")) {
			try {
				photo = storageService.getSignedUrl(imagesBucket, photo, 600);
			} catch (IOException e) {
				// If signing fails, return the stored value (could be a key or a URL)
			}
		}

		return AttractionResponse.builder()
				.attractionId(a.getAttractionId())
				.name(a.getName())
				.description(a.getDescription())
				.funFact(a.getFunFact())
				.photo2dUrl(photo)
				.glbUrl(a.getGlbUrl())
				.isActive(a.isActive())
				.build();
    }
	
	public AttractionResponse addAttraction(AttractionRequest request,
        MultipartFile image, MultipartFile model) throws IOException {

		Attraction attraction = Attraction.builder()
			.name(request.getAttractionName())
			.description(request.getAttractionDescription())
			.funFact(request.getAttractionFunFact())
			.build();
		
		Attraction saved = attractionRepository.save(attraction);
		
		if (image != null && !image.isEmpty()) {
            // compress image then upload to storage service
            byte[] compressed = compressImage(image, 0.8f, 1600, 1600);
            String fileName = "attraction-" + saved.getAttractionId() + ".jpg";
            try (InputStream in = new ByteArrayInputStream(compressed)) {
                String url = storageService.upload(in, "image/jpeg", imagesBucket, fileName);
                saved.setPhoto2dUrl(url);
                saved = attractionRepository.save(saved);
            }
        }
		
		if (model != null && !model.isEmpty()) {
            String filename = Optional.ofNullable(model.getOriginalFilename())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf(".")))
                .orElse("");
            if (!".glb".equalsIgnoreCase(filename)) {
                throw new IllegalArgumentException("Model must be a .glb file");
            }
			String modelFileName = "attraction-" + saved.getAttractionId() + ".glb";
			// attempt to compress with gltfpack (CLI) then upload compressed output; fallback to original
			Path tmpDir = null;
			Path inTmp = null;
			Path outTmp = null;
			boolean uploaded = false;
			try {
				tmpDir = Files.createTempDirectory("gltfpack");
				inTmp = tmpDir.resolve("in.glb");
				outTmp = tmpDir.resolve("out.glb");
				model.transferTo(inTmp.toFile());

				boolean canRunGltfpack = false;
				Path gltfExecPath = null;
				if (gltfpackPath != null && !gltfpackPath.isBlank()) {
					try {
						gltfExecPath = Paths.get(gltfpackPath);
						if (Files.exists(gltfExecPath) && Files.isRegularFile(gltfExecPath)) {
							canRunGltfpack = true;
							if (!Files.isExecutable(gltfExecPath)) {
								log.warn("gltfpack found at {} but is not marked executable; will attempt to run anyway", gltfExecPath);
							} else {
								log.debug("gltfpack executable found at {}", gltfExecPath);
							}
						} else {
							log.info("gltfpack not found at {}, skipping model compression", gltfpackPath);
						}
					} catch (InvalidPathException ex) {
						log.warn("Invalid gltfpack path '{}', skipping compression", gltfpackPath);
					}
				} else {
					log.debug("gltfpackPath is empty, skipping model compression");
				}

				Process p = null;
				if (canRunGltfpack) {
					String[] parts = gltfpackPath.trim().split("\\s+");
					String exe = parts[0].replaceAll("^\"|\"$", "");
					java.util.List<String> cmd = new java.util.ArrayList<>();
					cmd.add(exe);
					for (int i = 1; i < parts.length; i++) {
						if (!parts[i].isBlank()) cmd.add(parts[i]);
					}
					cmd.add("-i"); cmd.add(inTmp.toString()); cmd.add("-o"); cmd.add(outTmp.toString());
					log.debug("Running gltfpack command: {}", cmd);
					ProcessBuilder pb = new ProcessBuilder(cmd);
					pb.redirectErrorStream(true);
					p = pb.start();
				}
				String procOut = "";
				int rc = -1;
				if (p != null) {
					procOut = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
					rc = p.waitFor();
				}
				if (rc == 0 && Files.exists(outTmp)) {
					try (InputStream in = Files.newInputStream(outTmp)) {
						String url = storageService.upload(in, "model/gltf-binary", modelsBucket, modelFileName);
						saved.setGlbUrl(url);
						saved = attractionRepository.save(saved);
						uploaded = true;
					}
				} else {
					if (p == null) {
						log.debug("Skipped running gltfpack; uploading original model");
					} else {
						log.error("gltfpack failed (rc={}): {}", rc, procOut);
					}
				}
			} catch (IOException | InterruptedException e) {
				System.err.printf("gltfpack error: %s\n", e.getMessage());
				if (e instanceof InterruptedException) Thread.currentThread().interrupt();
			}

			if (!uploaded) {
				// model may have been moved by transferTo; prefer reading from the temp file if available
				try (InputStream in = (inTmp != null && Files.exists(inTmp)) ? Files.newInputStream(inTmp) : model.getInputStream()) {
					String url = storageService.upload(in, model.getContentType() == null ? "application/octet-stream" : model.getContentType(), modelsBucket, modelFileName);
					saved.setGlbUrl(url);
					saved = attractionRepository.save(saved);
				}
			}

			// cleanup temp files/directories after upload/fallback
			try {
				if (tmpDir != null && Files.exists(tmpDir)) {
					Files.list(tmpDir).forEach(p -> { try { Files.deleteIfExists(p); } catch (IOException ignore) {} });
					Files.deleteIfExists(tmpDir);
				}
			} catch (IOException ignore) {}
        }
		
		logIfStaffOrAdmin("Attraction", saved.getAttractionId().longValue(),
		LogAction.CREATE, null, saved);
		
		return toResponse(saved);
	}

	@Transactional(readOnly = true)
	public AttractionResponse getAttraction(int id) {
		Attraction attraction = attractionRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Attraction with ID '"+ id + "' not found."));
        return toResponse(attraction);
	}
	
	@Transactional(readOnly = true)
	public List<AttractionResponse> searchAttractions(String name, Boolean active) {
	    if (name == null) name = "";
	    List<Attraction> results;
	    if (active == null) {
	        results = attractionRepository.findByNameContainingIgnoreCaseOrderByName(name);
	    } else {
	        results = attractionRepository.findByNameContainingIgnoreCaseAndIsActiveOrderByName(name, active);
	    }
	    return results.stream().map(this::toResponse).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<AttractionResponse> getAllAttractions() {
		return attractionRepository.findAllByOrderByName()
	            .stream()
	            .map(this::toResponse)
	            .collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<AttractionResponse> getAllActiveAttractions() {
		return attractionRepository.findAllByIsActiveOrderByName(true)
	            .stream()
	            .map(this::toResponse)
	            .collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<AttractionResponse> getAllInactiveAttractions() {
		return attractionRepository.findAllByIsActiveOrderByName(false)
	            .stream()
	            .map(this::toResponse)
	            .collect(Collectors.toList());
	}
	
	public AttractionResponse updateAttraction(int id,
            AttractionRequest request,
            MultipartFile image, 
            MultipartFile model) throws IOException {
		Attraction existingAttraction = attractionRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(
					"Attraction with ID '" + id + "' not found."
		));
		
		String name = request.getAttractionName();
		String description = request.getAttractionDescription();
		String funFact = request.getAttractionFunFact();
		
		name = (name != null) ? name.trim() : null;
		description = (description != null) ? description.trim() : null;
		funFact = (funFact != null) ? funFact.trim() : null;
		
		if (name != null && name.length() < 1) {
			throw new IllegalArgumentException(
					"Attraction name must have at least 1 character."
					);
		}
		if (description != null && description.length() < 10) {
			throw new IllegalArgumentException(
					"Attraction description must be at least 10 characters long."
					);
		}
		
		boolean hasTextChange =
		(name != null && !existingAttraction.getName().equals(name)) ||
		(description != null && !existingAttraction.getDescription().equals(description)) ||
		(funFact != null &&
		!((existingAttraction.getFunFact() == null ? "" : existingAttraction.getFunFact())
		.equals(funFact)));
		
		boolean hasImageChange = image != null && !image.isEmpty();
		boolean hasModelChange = model != null && !model.isEmpty();
		
		if (!hasTextChange && !hasImageChange && !hasModelChange) {
		    throw new IllegalArgumentException("No changes detected for the attraction.");
		}
		
		Attraction prevAttraction = createAttractionCopy(existingAttraction);
		
		if (name != null) {
			existingAttraction.setName(name);
		}
		
		if (description != null) {
			existingAttraction.setDescription(description);
		}
		
		if (funFact != null) {
			existingAttraction.setFunFact(funFact);
		}
		
		if (hasImageChange) {
			// compress and upload
			byte[] compressed = compressImage(image, 0.8f, 1600, 1600);
			String fileName = "attraction-" + existingAttraction.getAttractionId() + ".jpg";
			try (InputStream in = new ByteArrayInputStream(compressed)) {
				String url = storageService.upload(in, "image/jpeg", imagesBucket, fileName);
				existingAttraction.setPhoto2dUrl(url);
			}
		}
		
		if (hasModelChange) {
		String filename = Optional.ofNullable(model.getOriginalFilename())
			.filter(f -> f.contains("."))
			.map(f -> f.substring(f.lastIndexOf(".")))
			.orElse("");
		if (!".glb".equalsIgnoreCase(filename)) {
			throw new IllegalArgumentException("Model must be a .glb file");
		}
		String modelFileName = "attraction-" + existingAttraction.getAttractionId() + ".glb";
		Path tmpDir = null;
		Path inTmp = null;
		Path outTmp = null;
		boolean uploaded = false;
		try {
			tmpDir = Files.createTempDirectory("gltfpack");
			inTmp = tmpDir.resolve("in.glb");
			outTmp = tmpDir.resolve("out.glb");
			model.transferTo(inTmp.toFile());

			boolean canRunGltfpack = false;
			Path gltfExecPath = null;
			if (gltfpackPath != null && !gltfpackPath.isBlank()) {
				try {
					gltfExecPath = Paths.get(gltfpackPath);
					if (Files.exists(gltfExecPath) && Files.isRegularFile(gltfExecPath)) {
						canRunGltfpack = true;
						if (!Files.isExecutable(gltfExecPath)) {
							log.info("gltfpack found at {} but is not marked executable; attempting to run", gltfExecPath);
						} else {
							log.info("gltfpack executable found at {}", gltfExecPath);
						}
					} else {
						log.info("gltfpack not found at {}, skipping model compression", gltfpackPath);
					}
				} catch (InvalidPathException ex) {
					log.warn("Invalid gltfpack path '{}', skipping compression", gltfpackPath);
				}
			} else {
				log.debug("gltfpackPath is empty, skipping model compression");
			}

			Process p = null;
			if (canRunGltfpack) {
				String[] parts = gltfpackPath.trim().split("\\s+");
				String exe = parts[0].replaceAll("^\"|\"$", "");
				java.util.List<String> cmd = new java.util.ArrayList<>();
				cmd.add(exe);
				for (int i = 1; i < parts.length; i++) {
					if (!parts[i].isBlank()) cmd.add(parts[i]);
				}
				cmd.add("-i"); cmd.add(inTmp.toString()); cmd.add("-o"); cmd.add(outTmp.toString());
				log.debug("Running gltfpack command: {}", cmd);
				ProcessBuilder pb = new ProcessBuilder(cmd);
				pb.redirectErrorStream(true);
				p = pb.start();
			}
			String procOut = "";
			int rc = -1;
			if (p != null) {
				procOut = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
				rc = p.waitFor();
			}
			if (rc == 0 && Files.exists(outTmp)) {
				try (InputStream in = Files.newInputStream(outTmp)) {
					String url = storageService.upload(in, "model/gltf-binary", modelsBucket, modelFileName);
					existingAttraction.setGlbUrl(url);
					uploaded = true;
				}
			} else {
				if (p == null) {
					log.debug("Skipped running gltfpack; uploading original model");
				} else {
					log.error("gltfpack failed (rc={}): {}", rc, procOut);
				}
			}
		} catch (IOException | InterruptedException e) {
			System.err.printf("gltfpack error: %s\n", e.getMessage());
			if (e instanceof InterruptedException) Thread.currentThread().interrupt();
		}

		if (!uploaded) {
			// prefer reading from temp file if transferTo moved the multipart
			try (InputStream in = (inTmp != null && Files.exists(inTmp)) ? Files.newInputStream(inTmp) : model.getInputStream()) {
				String url = storageService.upload(in, model.getContentType() == null ? "application/octet-stream" : model.getContentType(), modelsBucket, modelFileName);
				existingAttraction.setGlbUrl(url);
			}
		}

		// cleanup temp files/directories after upload/fallback
		try {
			if (tmpDir != null && Files.exists(tmpDir)) {
				Files.list(tmpDir).forEach(p -> { try { Files.deleteIfExists(p); } catch (IOException ignore) {} });
				Files.deleteIfExists(tmpDir);
			}
		} catch (IOException ignore) {}
	    }
		
		Attraction updated = attractionRepository.save(existingAttraction);
		
		logIfStaffOrAdmin(
			"Attraction",
			updated.getAttractionId().longValue(),
			LogAction.UPDATE,
			prevAttraction,
			updated
		);
		
		return toResponse(updated);
	}
	
	public void softDeleteAttraction(int id) {
		Attraction attraction = attractionRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Attraction with ID '"+ id + "' not found."));
		
		if (!attraction.isActive()) {
	        throw new IllegalStateException("Attraction is already disabled.");
	    }
		
		Attraction prevAttraction = createAttractionCopy(attraction);

		attraction.setActive(false);
		attractionRepository.save(attraction);
		
		logIfStaffOrAdmin("Attraction", (long) id, LogAction.DISABLE, prevAttraction, attraction);
	}
	
	public void hardDeleteAttraction(int id) {
		Attraction attraction = attractionRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Attraction with ID '"+ id + "' not found."));
		// attempt to remove associated files from storage (ignore failures)
		try {
			storageService.delete(imagesBucket, "attraction-" + id + ".jpg");
		} catch (IOException e) {
			System.err.printf("Failed to delete image for attraction %d: %s\n", id, e.getMessage());
		}
		try {
			storageService.delete(modelsBucket, "attraction-" + id + ".glb");
		} catch (IOException e) {
			System.err.printf("Failed to delete model for attraction %d: %s\n", id, e.getMessage());
		}
		
		logIfStaffOrAdmin("Attraction", (long) id, LogAction.DELETE, attraction, null);
		attractionRepository.delete(attraction);
	}
	
	public void restoreAttraction(int id) {
		Attraction attraction = attractionRepository.findById(id)
		        .orElseThrow(() -> new EntityNotFoundException("Attraction ID '" + id + "' not found."));

	    if (attraction.isActive()) {
	        throw new IllegalStateException("Account is already active.");
	    }
	    
	    Attraction prevAttraction = createAttractionCopy(attraction);

	    attraction.setActive(true);
	    attractionRepository.save(attraction);
	    
	    logIfStaffOrAdmin("Attraction", (long) id, LogAction.RESTORE, prevAttraction, attraction);
	}
}