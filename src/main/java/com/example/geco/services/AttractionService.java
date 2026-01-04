package com.example.geco.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	@Value("${app.upload-dir.attractions:C:/sts-4.32.0.RELEASE/dev/geco/uploads/attractions}")
	private String uploadAttractionImgDir;
	
	@Value("${app.upload-dir.attractions.models:C:/sts-4.32.0.RELEASE/dev/geco/uploads/attractions/models}")
	private String uploadAttractionModelDir;
	
	@Autowired
	AttractionRepository attractionRepository;

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
		return AttractionResponse.builder()
				.attractionId(a.getAttractionId())
				.name(a.getName())
				.description(a.getDescription())
				.funFact(a.getFunFact())
				.photo2dUrl(a.getPhoto2dUrl())
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
			String ext = Optional.ofNullable(image.getOriginalFilename())
				.filter(f -> f.contains("."))
				.map(f -> f.substring(f.lastIndexOf(".")))
				.orElse("");
			
			Path uploadPath = Paths.get(uploadAttractionImgDir);
			Files.createDirectories(uploadPath);
			
			String fileName = "attraction-" + saved.getAttractionId() + ext;
			Path target = uploadPath.resolve(fileName);
			image.transferTo(target.toFile());
			
			saved.setPhoto2dUrl("/uploads/attractions/" + fileName);
			saved = attractionRepository.save(saved);  // update with photo URL
		}
		
		if (model != null && !model.isEmpty()) {
	        String filename = Optional.ofNullable(model.getOriginalFilename())
	            .filter(f -> f.contains("."))
	            .map(f -> f.substring(f.lastIndexOf(".")))
	            .orElse("");
	        // validate extension .glb (case-insensitive)
	        if (!".glb".equalsIgnoreCase(filename)) {
	            throw new IllegalArgumentException("Model must be a .glb file");
	        }
	
	        Path uploadPath = Paths.get(uploadAttractionModelDir);
	        Files.createDirectories(uploadPath);
	
	        String modelFileName = "attraction-" + saved.getAttractionId() + ".glb";
	        Path target = uploadPath.resolve(modelFileName);
	        model.transferTo(target.toFile());
	
	        saved.setGlbUrl("/uploads/models/" + modelFileName);
	        saved = attractionRepository.save(saved);
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
            MultipartFile image, MultipartFile model) throws IOException {
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
		
		if (!hasTextChange && !hasImageChange) {
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
			String ext = Optional.ofNullable(image.getOriginalFilename())
					.filter(f -> f.contains("."))
					.map(f -> f.substring(f.lastIndexOf(".")))
					.orElse("");
		
			Path uploadPath = Paths.get(uploadAttractionImgDir);
			Files.createDirectories(uploadPath);
			
			String fileName = "attraction-" + existingAttraction.getAttractionId() + ext;
			Path target = uploadPath.resolve(fileName);
			image.transferTo(target.toFile());
			
			existingAttraction.setPhoto2dUrl("/uploads/attractions/" + fileName);
		}
		
		if (hasModelChange) {
	        String filename = Optional.ofNullable(model.getOriginalFilename())
	            .filter(f -> f.contains("."))
	            .map(f -> f.substring(f.lastIndexOf(".")))
	            .orElse("");
	        // validate extension .glb (case-insensitive)
	        if (!".glb".equalsIgnoreCase(filename)) {
	            throw new IllegalArgumentException("Model must be a .glb file");
	        }
	
	        Path uploadPath = Paths.get(uploadAttractionModelDir);
	        Files.createDirectories(uploadPath);
	
	        String modelFileName = "attraction-" + existingAttraction.getAttractionId() + ".glb";
	        Path target = uploadPath.resolve(modelFileName);
	        model.transferTo(target.toFile());
	
	        existingAttraction.setGlbUrl("/uploads/models/" + modelFileName);
	        existingAttraction = attractionRepository.save(existingAttraction);
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