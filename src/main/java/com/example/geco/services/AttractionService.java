package com.example.geco.services;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	
	@Autowired
	AttractionRepository attractionRepository;

	private byte[] compressImage(MultipartFile file, float quality, int maxWidth, int maxHeight) throws IOException {
		BufferedImage img = ImageIO.read(file.getInputStream());
		if (img == null) throw new IOException("Invalid image file");

		int width = img.getWidth();
		int height = img.getHeight();

		double scale = 1.0;
		if (maxWidth > 0 && width > maxWidth) scale = Math.min(scale, (double) maxWidth / width);
		if (maxHeight > 0 && height > maxHeight) scale = Math.min(scale, (double) maxHeight / height);

		int newW = (int) Math.max(1, Math.round(width * scale));
		int newH = (int) Math.max(1, Math.round(height * scale));

		BufferedImage outImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = outImg.createGraphics();
		g2d.drawImage(img, 0, 0, newW, newH, null);
		g2d.dispose();

		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
		if (!writers.hasNext()) throw new IOException("No JPEG writer available");
		ImageWriter writer = writers.next();

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
			ImageWriteParam param = writer.getDefaultWriteParam();
			if (param.canWriteCompressed()) {
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(quality);
			}
			writer.setOutput(ios);
			writer.write(null, new IIOImage(outImg, null, null), param);
			ios.flush();
			return baos.toByteArray();
		} finally {
			writer.dispose();
		}
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
            try (InputStream in = model.getInputStream()) {
                String url = storageService.upload(in, model.getContentType() == null ? "application/octet-stream" : model.getContentType(), modelsBucket, modelFileName);
                saved.setGlbUrl(url);
                saved = attractionRepository.save(saved);
            }
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
	        try (InputStream in = model.getInputStream()) {
	            String url = storageService.upload(in, model.getContentType() == null ? "application/octet-stream" : model.getContentType(), modelsBucket, modelFileName);
	            existingAttraction.setGlbUrl(url);
	        }
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