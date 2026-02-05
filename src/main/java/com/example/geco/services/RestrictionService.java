package com.example.geco.services;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.geco.domains.AuditLog.LogAction;
import com.example.geco.domains.Restriction;
import com.example.geco.dto.RestrictionRequest;
import com.example.geco.dto.RestrictionUpdateRequest;
import com.example.geco.repositories.RestrictionRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RestrictionService extends BaseService{
	@Autowired
	RestrictionRepository restrictionRepository;
	
	@Autowired
	CalendarDateService calendarDateService;
	
	public Restriction createRestrictionCopy(Restriction restriction) {
		return Restriction.builder()
				.id(restriction.getId())
				.name(restriction.getName())
				.value(restriction.getValue())
				.build();
	}
	
	public Restriction addRestriction(RestrictionRequest request) {
		Restriction restriction = Restriction.builder()
				.name(request.getName().trim().toUpperCase())
				.value(request.getValue())
				.build();
		
		Restriction savedRestriction = restrictionRepository.save(restriction);
				
		logIfStaffOrAdmin("Restriction", (long)savedRestriction.getId(), LogAction.CREATE, null, savedRestriction);
		
		return savedRestriction;
	}
	
	@Transactional(readOnly = true)
	public Restriction getRestriction(Integer id) {
		if (id == null) {
			throw new IllegalArgumentException("Restriction ID is missing.");
		}
		
		return restrictionRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Restriction with ID \"" + id + "\" not found."));
	}
	
	@Transactional(readOnly = true)
	public Restriction getRestriction(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Restriction name is missing.");
		}
		
		String normalized = name.trim();

        return restrictionRepository.findByNameIgnoreCase(normalized)
            .orElseThrow(() -> new EntityNotFoundException(
                "Restriction with name \"" + normalized + "\" not found."
            ));
	}
	
	@Transactional
	public Restriction updateRestriction(Integer id, RestrictionUpdateRequest request) {
	    if (id == null) {
	        throw new IllegalArgumentException("Restriction ID is missing.");
	    }
	    if (request == null) {
	        throw new IllegalArgumentException("Restriction request is missing.");
	    }

	    Restriction restriction = restrictionRepository.findById(id)
	        .orElseThrow(() -> new EntityNotFoundException("Restriction with ID \"" + id + "\" not found."));

	    String rawName = request.getName();
	    String normalizedName = (rawName == null) ? null : rawName.trim();

	    boolean nameProvided = rawName != null;               
	    boolean nameUsable = normalizedName != null && !normalizedName.isEmpty();
	    boolean valueProvided = request.getValue() != null;

	    if (!nameUsable && !valueProvided) {
	        throw new IllegalArgumentException("No fields to update.");
	    }

	    // If client sent name but it's blank, and they are also updating value, still reject the blank name.
	    if (nameProvided && !nameUsable && valueProvided) {
	        throw new IllegalArgumentException("Restriction name is missing.");
	    }

	    Restriction before = createRestrictionCopy(restriction);
	    Boolean changed = false;

	    if (nameUsable) {
	        String currentName = restriction.getName();
	        boolean sameName = currentName != null && currentName.equalsIgnoreCase(normalizedName);

	        if (!sameName) {
	            restrictionRepository.findByNameIgnoreCase(normalizedName).ifPresent(existing -> {
	                if (!existing.getId().equals(id)) {
	                    throw new EntityExistsException(
	                        "Restriction with name \"" + normalizedName + "\" already exists."
	                    );
	                }
	            });

	            restriction.setName(normalizedName);
	            changed = true;
	        }
	    }

	    if (valueProvided) {
	        Integer newValue = request.getValue();
	        if (newValue < 0) {
	            throw new IllegalArgumentException("Restriction value must be 0 or greater.");
	        }

	        Integer currentValue = restriction.getValue();
	        boolean sameValue = (currentValue == null) ? (newValue == null) : currentValue.equals(newValue);

	        if (!sameValue) {
	            restriction.setValue(newValue);
	            changed = true;
	        }
	    }
	    
	    if (!changed) {
	        throw new IllegalArgumentException("No changes detected.");
	    }

	    Restriction savedRestriction = restrictionRepository.save(restriction);
	    logIfStaffOrAdmin("Restriction", id.longValue(), LogAction.UPDATE, before, savedRestriction);

	    boolean isBookingLimit = savedRestriction.getName() != null
	        && savedRestriction.getName().trim().equalsIgnoreCase("booking limit");

	    if (isBookingLimit && !java.util.Objects.equals(before.getValue(), savedRestriction.getValue())) {
	        calendarDateService.updateCalendarDateBookingLimit(LocalDate.now(), before.getValue(), savedRestriction.getValue());
	    }
	    
	    return savedRestriction;
	}
}
