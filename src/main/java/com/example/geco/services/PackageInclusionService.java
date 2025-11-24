package com.example.geco.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.geco.domains.AuditLog.LogAction;
import com.example.geco.domains.PackageInclusion;
import com.example.geco.repositories.PackageInclusionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class PackageInclusionService extends BaseService{
	@Autowired
	PackageInclusionRepository packageInclusionRepository;
	
	public PackageInclusion createInclusionCopy(PackageInclusion inclusion) {
		return PackageInclusion.builder()
				.inclusionId(inclusion.getInclusionId())
				.inclusionName(inclusion.getInclusionName())
				.inclusionPricePerPerson(inclusion.getInclusionPricePerPerson())
				.isActive(inclusion.isActive())
				.build();
	}
	
	public PackageInclusion addInclusion(PackageInclusion inclusion) {
		if (inclusion.getInclusionName() == null || inclusion.getInclusionName().trim().isBlank()) {
	        throw new IllegalArgumentException("Inclusion name is missing.");
	    }
		
		String name = inclusion.getInclusionName().trim();
		inclusion.setInclusionName(name);
		
		if (inclusion.getInclusionPricePerPerson() == null) {
	        throw new IllegalArgumentException("Inclusion price per person is missing.");
	    }
		
		if (inclusion.getInclusionPricePerPerson() < 0) {
	        throw new IllegalArgumentException("Invalid Inclusion price per person.");
	    }
		
		if (packageInclusionRepository.existsByInclusionNameIgnoreCase(name)) {
	        throw new IllegalArgumentException("Inclusion name '" + name + "' already exists.");
	    }
		
		PackageInclusion savedInclusion = packageInclusionRepository.save(inclusion);
		
		logIfStaffOrAdmin("PackageInclusion", (long) savedInclusion.getInclusionId(), LogAction.CREATE, null, savedInclusion);
		
		return savedInclusion;
	}
	
	public PackageInclusion getInclusion(int id) {
		return packageInclusionRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Package Inclusion with ID '" + id + "' not found."));
	}
	
	public List<PackageInclusion> getAllInclusions() {
		return packageInclusionRepository.findAllByOrderByInclusionName();
	}
	
	public List<PackageInclusion> getAllActiveInclusions() {
		return packageInclusionRepository.findAllByIsActiveOrderByInclusionName(true);
	}
	
	public List<PackageInclusion> getAllInactiveInclusions() {
		return packageInclusionRepository.findAllByIsActiveOrderByInclusionName(false);
	}
	
	@Transactional
	public PackageInclusion updateInclusion(int id, PackageInclusion inclusion) {
		if (inclusion.getInclusionName() == null && inclusion.getInclusionPricePerPerson() == null) {
			throw new IllegalArgumentException("Package Inclusion name and price per person are missing.");
		}
		
		if (inclusion.getInclusionPricePerPerson() != null && inclusion.getInclusionPricePerPerson() < 0) {
			throw new IllegalArgumentException("Package Inclusion price cannot be less than 0.");
		}
		
		PackageInclusion existingInclusion = packageInclusionRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Package Inclusion with ID '" + id + "' not found."));
		
		PackageInclusion prevInclusion = createInclusionCopy(existingInclusion);

		if (inclusion.getInclusionName() != null && !inclusion.getInclusionName().trim().isBlank()) {
			String newName = inclusion.getInclusionName().trim();
			
			// Does a record exist with this name but with a different ID?
			if (packageInclusionRepository.existsByInclusionNameIgnoreCaseAndInclusionIdNot(newName, id)) {
		        throw new IllegalArgumentException("Inclusion name '" + newName + "' already exists.");
		    }
			
			existingInclusion.setInclusionName(newName);
	    }
		
		if (inclusion.getInclusionPricePerPerson() != null && inclusion.getInclusionPricePerPerson() >= 0) {
			existingInclusion.setInclusionPricePerPerson(inclusion.getInclusionPricePerPerson());
	    }
		
		logIfStaffOrAdmin("PackageInclusion", (long) id, LogAction.UPDATE, prevInclusion, existingInclusion);
		
		return packageInclusionRepository.save(existingInclusion);
	}

	@Transactional
	public void softDeleteInclusion(int id) {
		PackageInclusion inclusion = packageInclusionRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Package Inclusion with ID '" + id + "' not found."));
	    
		if (!inclusion.isActive()) {
	        throw new IllegalStateException("Inclusion is already disabled.");
	    }
		
		PackageInclusion prevInclusion = createInclusionCopy(inclusion);

		inclusion.setActive(false);
		packageInclusionRepository.save(inclusion);
		
		logIfStaffOrAdmin("PackageInclusion", (long) id, LogAction.DISABLE, prevInclusion, inclusion);
	}

	@Transactional
	public void restoreInclusion(int id) {
		PackageInclusion inclusion = packageInclusionRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Package Inclusion with ID '" + id + "' not found."));
	    
		if (inclusion.isActive()) {
	        throw new IllegalStateException("Inclusion is already active.");
	    }
		
		PackageInclusion prevInclusion = createInclusionCopy(inclusion);

		inclusion.setActive(true);
		packageInclusionRepository.save(inclusion);
		
		logIfStaffOrAdmin("PackageInclusion", (long) id, LogAction.RESTORE, prevInclusion, inclusion);
	}
}
