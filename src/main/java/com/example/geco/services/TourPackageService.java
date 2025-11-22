package com.example.geco.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.geco.domains.TourPackage;
import com.example.geco.repositories.TourPackageRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TourPackageService {
	@Autowired
	TourPackageRepository tourPackageRepository;
	
	public TourPackage addPackage(TourPackage tourPackage) {
		if (tourPackage.getDescription() == null || tourPackage.getDescription().trim().isBlank()) {
	        throw new IllegalArgumentException("Description is missing.");
	    }
		
		if (tourPackage.getName().trim().length() == 0) {
	        throw new IllegalArgumentException("Name must at least have one character.");
	    }
		tourPackage.setName(tourPackage.getName().trim());
		
		
		if (tourPackage.getDescription().trim().length() < 10) {
	        throw new IllegalArgumentException("Description must have at least 10 characters.");
	    }
		tourPackage.setDescription(tourPackage.getDescription().trim());
		
		if (tourPackage.getBasePrice() == null) {
	        throw new IllegalArgumentException("Base price for the package is missing.");
	    }
		
		if (tourPackage.getBasePrice() < 0) {
	        throw new IllegalArgumentException("Invalid Base Price for the package.");
	    }
		
		if(tourPackage.getInclusions() == null || tourPackage.getInclusions().isEmpty()) {
	        throw new IllegalArgumentException("Package Inclusions are missing.");
		}
		
		return tourPackageRepository.save(tourPackage);
	}
	
	public TourPackage getPackage(int id) {
		return tourPackageRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Package with ID \"" + id + "\" not found."));
	}
	
	public List<TourPackage> getAllPackages() {
		return tourPackageRepository.findAllByOrderByName();
	}
	
	public TourPackage updatePackage(TourPackage tourPackage) {
		if (tourPackage.getName() == null && tourPackage.getDescription() == null && tourPackage.getBasePrice() == null && tourPackage.getInclusions() == null) {
			throw new IllegalArgumentException("Package description, base price, and inclusions are missing.");
		}
		
		TourPackage existingTourPackage = tourPackageRepository.findById(tourPackage.getPackageId())
	            .orElseThrow(() -> new EntityNotFoundException("Package with ID \"" + tourPackage.getPackageId() + "\" not found."));
		
		if (tourPackage.getName() != null && !tourPackage.getName().isBlank()) {
			existingTourPackage.setName(tourPackage.getName().trim());
		}
		
		if (tourPackage.getDescription() != null && !tourPackage.getDescription().isBlank()) {
			existingTourPackage.setDescription(tourPackage.getDescription().trim());
		}

		if (tourPackage.getBasePrice() != null && tourPackage.getBasePrice() >= 0) {
			existingTourPackage.setBasePrice(tourPackage.getBasePrice());
		}
		
		if (tourPackage.getInclusions() != null) {
		    if (tourPackage.getInclusions().isEmpty()) {
		        throw new IllegalArgumentException("Package inclusions cannot be empty.");
		    }
		    
		    existingTourPackage.setInclusions(tourPackage.getInclusions());
		}
		
		return tourPackageRepository.save(existingTourPackage);
	}
	
	public void deletePackage(int id) {
		TourPackage tourPackage = tourPackageRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Package with ID \"" + id + "\" not found."));
	    
		// Detach inclusions to avoid constraint issues.
		if (tourPackage.getInclusions() != null) {
	        tourPackage.getInclusions().clear();
	    }
		
		tourPackageRepository.delete(tourPackage);
	}
}
