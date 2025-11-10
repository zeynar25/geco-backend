package com.example.geco.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.geco.domains.Attraction;
import com.example.geco.dto.AttractionResponse;
import com.example.geco.repositories.AttractionRepository;

@Service
public class AttractionService {
	@Autowired
	AttractionRepository attractionRepository;
	
	public double getAttractionsNumber() {
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
	    return toResponse(a);
	}
	
	public AttractionResponse getAttraction(int id) {
		Attraction attraction = attractionRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Attraction not found."));
        return toResponse(attraction);
	}
	
	public List<AttractionResponse> getAllAttractions() {
		return attractionRepository.findAll()
	            .stream()
	            .map(this::toResponse)
	            .collect(Collectors.toList());
	}
	
	public AttractionResponse updateAttraction(Attraction attraction) {
		Attraction existingAttraction = attractionRepository.findById(attraction.getAttractionId())
				.orElseThrow(() -> new IllegalArgumentException("Attraction not found."));
		
		validateAttraction(attraction);
		existingAttraction.setName(attraction.getName());
		existingAttraction.setDescription(attraction.getDescription());

		Attraction updated = attractionRepository.save(existingAttraction);
		
        return toResponse(updated);
	}
	
	public AttractionResponse deleteAttraction(int id) {
		Attraction attraction = attractionRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Attraction not found."));;
		
		attractionRepository.delete(attraction);
		
		return toResponse(attraction);
	}
}