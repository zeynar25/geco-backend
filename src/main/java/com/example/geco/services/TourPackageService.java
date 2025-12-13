package com.example.geco.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.geco.domains.AuditLog.LogAction;
import com.example.geco.domains.PackageInclusion;
import com.example.geco.domains.TourPackage;
import com.example.geco.dto.TourPackageRequest;
import com.example.geco.dto.TourPackageUpdateRequest;
import com.example.geco.repositories.PackageInclusionRepository;
import com.example.geco.repositories.TourPackageRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class TourPackageService extends BaseService{
	@Autowired
	TourPackageRepository tourPackageRepository;
	
	@Autowired
	PackageInclusionRepository inclusionRepository;
	
	
	public TourPackage createTourPackageCopy(TourPackage tourPackage) {
		return TourPackage.builder()
				.packageId(tourPackage.getPackageId())
				.name(tourPackage.getName())
				.duration(tourPackage.getDuration())
				.description(tourPackage.getDescription())
				.basePrice(tourPackage.getBasePrice())
				.minPerson(tourPackage.getMinPerson())
				.maxPerson(tourPackage.getMaxPerson())
				.inclusions(
						tourPackage.getInclusions() != null
				        ? List.copyOf(tourPackage.getInclusions())
				                : null
				)
				.isActive(tourPackage.isActive())
			    .build();
	}
	
	
	
	public TourPackage addPackage(TourPackageRequest request) {
		String name = request.getName().trim();
		String description = request.getDescription().trim();
		Integer duration = request.getDuration();
		Integer basePrice = request.getBasePrice();
		Integer minPerson = request.getMinPerson();
		Integer maxPerson= request.getMaxPerson();
		String notes = request.getDescription() != null ? request.getDescription().trim() : null;
		List<Integer> inclusionIds = request.getInclusionIds();
		
		List<PackageInclusion> inclusions =
				inclusionRepository.findAllByInclusionIdIn(inclusionIds);

	    if (inclusions.size() != inclusionIds.size()) {
	        throw new IllegalArgumentException("Some inclusion IDs do not exist.");
	    }
	    
		TourPackage tourPackage = TourPackage.builder()
				.name(name)
				.description(description)
				.duration(duration)
				.basePrice(basePrice)
				.minPerson(minPerson)
				.maxPerson(maxPerson)
				.notes(notes)
				.inclusions(inclusions)
				.build();
		
		TourPackage savedPackage = tourPackageRepository.save(tourPackage);
		
		logIfStaffOrAdmin("TourPackage", (long) savedPackage.getPackageId(), LogAction.CREATE, null, savedPackage);
		
		return savedPackage;
	}
	
	@Transactional(readOnly = true)
	public Long getPackagesNumber() {
		return tourPackageRepository.count();
	}
	
    @Transactional(readOnly = true)
	public TourPackage getPackage(int id) {
		return tourPackageRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Tour package with ID '" + id + "' not found."));
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
    
    @Transactional(readOnly = true)
    public List<PackageInclusion> getAvailableInclusions(int id) {
        TourPackage tourPackage = tourPackageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tour package with ID '" + id + "' not found."));

        List<PackageInclusion> allActiveInclusions = inclusionRepository.findAllByIsActiveOrderByInclusionName(true);

        // Filter out inclusions already in this package
        return allActiveInclusions.stream()
                .filter(inclusion -> !tourPackage.getInclusions().contains(inclusion))
                .collect(Collectors.toList());
    }
	
	public TourPackage updatePackage(int id, TourPackageUpdateRequest request) {
		String name = request.getName() != null ? request.getName().trim() : null;
		String description = request.getDescription() != null ? request.getDescription().trim() : null;
		Integer duration = request.getDuration();
		Integer basePrice = request.getBasePrice();
		Integer minPerson = request.getMinPerson();
		Integer maxPerson = request.getMaxPerson();
		String notes = request.getNotes() != null ? request.getNotes().trim() : null;
		List<Integer> inclusionIds = request.getInclusionIds() != null ? request.getInclusionIds() : List.of();
		
		if (name == null 
				&& description == null 
				&& duration == null
				&& basePrice == null
				&& (inclusionIds == null || inclusionIds.isEmpty())) {
			throw new IllegalArgumentException("No update fields provided.");
		}
		
		TourPackage existingTourPackage = tourPackageRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Tour package with ID '" + id + "' not found."));
		
	    TourPackage prevTourPackage = createTourPackageCopy(existingTourPackage);
		
	    if (name != null) {
	        if (name.isBlank()) {
	            throw new IllegalArgumentException("Name cannot be blank.");
	        }
	        existingTourPackage.setName(name);
	    }

		
	    if (description != null) {
	        if (description.isBlank()) {
	            throw new IllegalArgumentException("Description cannot be blank.");
	        }
	        existingTourPackage.setDescription(description);
	    }
		
		if (duration != null) {
		    existingTourPackage.setDuration(duration);
		}


		if (basePrice != null) {
			existingTourPackage.setBasePrice(basePrice);
		}
		
		if (minPerson != null) {
		    existingTourPackage.setMinPerson(minPerson);
		}

		if (maxPerson != null) {
		    existingTourPackage.setMaxPerson(maxPerson);
		}
		
		if (notes != null) {
	        if (notes.isBlank()) {
	            throw new IllegalArgumentException("Notes cannot be blank.");
	        }
	        existingTourPackage.setNotes(notes);
	    }
		
		if (inclusionIds != null) {
			if (inclusionIds.isEmpty()) {
		        existingTourPackage.getInclusions().clear();
		        
		    } else {
				List<PackageInclusion> inclusions =
						inclusionRepository.findAllByInclusionIdIn(inclusionIds);

			    if (inclusions.size() != inclusionIds.size()) {
			        throw new IllegalArgumentException("Some inclusion IDs do not exist.");
			    }
			    
			    existingTourPackage.setInclusions(inclusions);
		    }
		}
		
		logIfStaffOrAdmin("TourPackage", (long) id, LogAction.UPDATE, prevTourPackage, existingTourPackage);
		
		return tourPackageRepository.save(existingTourPackage);
	}
	
	public void softDeletePackage(int id) {
		TourPackage tourPackage = tourPackageRepository.findById(id)
		        .orElseThrow(() -> new EntityNotFoundException("Tour package with ID '" + id + "' not found."));

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
	            .orElseThrow(() -> new EntityNotFoundException("Tour package with ID '" + id + "' not found."));

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
		        .orElseThrow(() -> new EntityNotFoundException("Tour package with ID '" + id + "' not found."));

	    if (tourPackage.isActive()) {
	        throw new IllegalStateException("Tour package is already active.");
	    }
	    
	    TourPackage prevTourPackage = createTourPackageCopy(tourPackage);

	    tourPackage.setActive(true);
	    tourPackageRepository.save(tourPackage);
	    
	    logIfStaffOrAdmin("TourPackage", (long) id, LogAction.RESTORE, prevTourPackage, tourPackage);
	}
}
