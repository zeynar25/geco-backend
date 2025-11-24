package com.example.geco.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.geco.domains.Account;
import com.example.geco.domains.Account.Role;
import com.example.geco.domains.AuditLog.LogAction;
import com.example.geco.domains.Attraction;
import com.example.geco.dto.AttractionResponse;
import com.example.geco.exceptions.AccessDeniedException;
import com.example.geco.repositories.AttractionRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AttractionService extends BaseService{
	@Autowired
	AttractionRepository attractionRepository;
	
	public long getAttractionsNumber() {
		return attractionRepository.count();
	}
	
	private void validateAttraction(Attraction attraction) {
		if (attraction.getName() == null || attraction.getName().trim().length() < 1) {
		    throw new IllegalArgumentException("Attraction name must have at least 1 character.");
		}
		
		if (attraction.getDescription() == null || attraction.getDescription().trim().length() < 10) {
		    throw new IllegalArgumentException("Attraction description must be at least 10 characters long.");
		}
	}
	
	public Attraction createAttractionCopy(Attraction a) {
		return Attraction.builder()
				.attractionId(a.getAttractionId())
				.name(a.getName())
				.description(a.getDescription())
				.isActive(a.isActive())
			    .build();
	}
	
	private AttractionResponse toResponse(Attraction a) {
		return new AttractionResponse(
				a.getAttractionId(),
				a.getName(),
				a.getDescription()
		);
    }
	
	public AttractionResponse addAttraction(Attraction attraction) {
		validateAttraction(attraction);
		Attraction a = attractionRepository.save(attraction);
		
		logIfStaffOrAdmin("Attraction", (long) a.getAttractionId(), LogAction.CREATE, null, a);
		
	    return toResponse(a);
	}
	
	public AttractionResponse getAttraction(int id) {
		Attraction attraction = attractionRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Attraction with ID '"+ id + "' not found."));
        return toResponse(attraction);
	}
	
	public List<AttractionResponse> getAllAttractions() {
		return attractionRepository.findAllByOrderByName()
	            .stream()
	            .map(this::toResponse)
	            .collect(Collectors.toList());
	}

	public List<AttractionResponse> getAllActiveAttractions() {
		return attractionRepository.findAllByIsActiveOrderByName(true)
	            .stream()
	            .map(this::toResponse)
	            .collect(Collectors.toList());
	}

	public List<AttractionResponse> getAllInactiveAttractions() {
		return attractionRepository.findAllByIsActiveOrderByName(false)
	            .stream()
	            .map(this::toResponse)
	            .collect(Collectors.toList());
	}
	
	public AttractionResponse updateAttraction(int id, Attraction attraction) {
		Attraction existingAttraction = attractionRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Attraction with ID '"+ id + "' not found."));

		if (attraction.getName() != null && attraction.getName().trim().length() < 1) {
	        throw new IllegalArgumentException("Attraction name must have at least 1 character.");
	    }

	    if (attraction.getDescription() != null && attraction.getDescription().trim().length() < 10) {
	        throw new IllegalArgumentException("Attraction description must be at least 10 characters long.");
	    }

	    String name = attraction.getName() != null ? attraction.getName().trim() : null;
	    String description = attraction.getDescription() != null ? attraction.getDescription().trim() : null;

	    if ((name == null || existingAttraction.getName().equals(name)) &&
	        (description == null || existingAttraction.getDescription().equals(description))) {
	        throw new IllegalArgumentException("No changes detected for the attraction.");
	    }

	    Attraction prevAttraction = createAttractionCopy(existingAttraction);

	    if (name != null) existingAttraction.setName(name);
	    if (description != null) existingAttraction.setDescription(description);

	    Attraction updated = attractionRepository.save(existingAttraction);

	    logIfStaffOrAdmin("Attraction", (long) updated.getAttractionId(), LogAction.UPDATE, prevAttraction, updated);

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