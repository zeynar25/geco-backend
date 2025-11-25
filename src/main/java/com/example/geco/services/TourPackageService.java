package com.example.geco.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.geco.domains.TourPackage;
import com.example.geco.domains.AuditLog.LogAction;
import com.example.geco.repositories.TourPackageRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class TourPackageService extends BaseService{
	@Autowired
	TourPackageRepository tourPackageRepository;
	
	
	public TourPackage createTourPackageCopy(TourPackage tourPackage) {
		return TourPackage.builder()
				.packageId(tourPackage.getPackageId())
				.name(tourPackage.getName())
				.duration(tourPackage.getDuration())
				.description(tourPackage.getDescription())
				.basePrice(tourPackage.getBasePrice())
				.inclusions(tourPackage.getInclusions())
				.isActive(tourPackage.isActive())
			    .build();
	}
	
	
	
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
		
		TourPackage savedPackage = tourPackageRepository.save(tourPackage);
		
		logIfStaffOrAdmin("TourPackage", (long) savedPackage.getPackageId(), LogAction.CREATE, null, savedPackage);
		
		
		return savedPackage;
	}
	
    @Transactional(readOnly = true)
	public TourPackage getPackage(int id) {
		return tourPackageRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Package with ID \"" + id + "\" not found."));
	}

    @Transactional(readOnly = true)
	public List<TourPackage> getAllPackages() {
		return tourPackageRepository.findAllByOrderByName();
	}

    @Transactional(readOnly = true)
	public List<TourPackage> getAllActivePackages() {
		return tourPackageRepository.findAllByIsActiveOrderByName(true);
	}

    @Transactional(readOnly = true)
	public List<TourPackage> getAllInactivePackages() {
		return tourPackageRepository.findAllByIsActiveOrderByName(false);
	}
	
	public TourPackage updatePackage(int id, TourPackage tourPackage) {
		if (tourPackage.getName() == null && tourPackage.getDescription() == null && tourPackage.getBasePrice() == null && tourPackage.getInclusions() == null) {
			throw new IllegalArgumentException("Package description, base price, and inclusions are missing.");
		}
		
		TourPackage existingTourPackage = tourPackageRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Package with ID \"" + id + "\" not found."));
		
	    TourPackage prevTourPackage = createTourPackageCopy(existingTourPackage);
		
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
		
		logIfStaffOrAdmin("TourPackage", (long) id, LogAction.UPDATE, prevTourPackage, existingTourPackage);
		
		return tourPackageRepository.save(existingTourPackage);
	}
	
	public void softDeletePackage(int id) {
		TourPackage tourPackage = tourPackageRepository.findById(id)
		        .orElseThrow(() -> new EntityNotFoundException("Tour package ID '" + id + "' not found."));

	    if (!tourPackage.isActive()) {
	        throw new IllegalStateException("Tour package is already disabled.");
	    }
	    
	    TourPackage prevTourPackage = createTourPackageCopy(tourPackage);

	    tourPackage.setActive(false);
	    tourPackageRepository.save(tourPackage);
	    
	    logIfStaffOrAdmin("TourPackage", (long) id, LogAction.DISABLE, prevTourPackage, tourPackage);
	}
	
	public void hardDeletePackage(int id) {
		TourPackage tourPackage = tourPackageRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Package with ID \"" + id + "\" not found."));

	    TourPackage prevTourPackage = createTourPackageCopy(tourPackage);
	    
		// Detach inclusions to avoid constraint issues.
		if (tourPackage.getInclusions() != null) {
	        tourPackage.getInclusions().clear();
	    }
		
		tourPackageRepository.delete(tourPackage);
		
		logIfStaffOrAdmin("TourPackage", (long) id, LogAction.DELETE, prevTourPackage, null);
	}

	public void restorePackage(int id) {
		TourPackage tourPackage = tourPackageRepository.findById(id)
		        .orElseThrow(() -> new EntityNotFoundException("Tour package ID '" + id + "' not found."));

	    if (tourPackage.isActive()) {
	        throw new IllegalStateException("Tour package is already active.");
	    }
	    
	    TourPackage prevTourPackage = createTourPackageCopy(tourPackage);

	    tourPackage.setActive(true);
	    tourPackageRepository.save(tourPackage);
	    
	    logIfStaffOrAdmin("TourPackage", (long) id, LogAction.RESTORE, prevTourPackage, tourPackage);
	}
}
