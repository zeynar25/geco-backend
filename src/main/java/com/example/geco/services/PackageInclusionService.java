package com.example.geco.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.geco.domains.PackageInclusion;
import com.example.geco.repositories.PackageInclusionRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PackageInclusionService {
	@Autowired
	PackageInclusionRepository packageInclusionRepository;
	
	public PackageInclusion addInclusion(PackageInclusion inclusion) {
		if (inclusion.getInclusionName() == null || inclusion.getInclusionName().trim().isBlank()) {
	        throw new IllegalArgumentException("Inclusion name is missing.");
	    }
		
		if (inclusion.getInclusionPricePerPerson() == null) {
	        throw new IllegalArgumentException("Inclusion price per person is missing.");
	    }
		
		if (inclusion.getInclusionPricePerPerson() < 0) {
	        throw new IllegalArgumentException("Invalid Inclusion price per person.");
	    }
		
		return packageInclusionRepository.save(inclusion);
	}
	
	public PackageInclusion getInclusion(int id) {
		return packageInclusionRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Package Inclusion with ID \"" + id + "\" not found."));
	}
	
	public List<PackageInclusion> getAllInclusions() {
		return packageInclusionRepository.findAllByOrderByInclusionName();
	}
	
	public PackageInclusion updateInclusion(PackageInclusion inclusion) {
		if (inclusion.getInclusionName() == null && inclusion.getInclusionPricePerPerson() == null) {
			throw new IllegalArgumentException("Package Inclusion name and price per person are missing.");
		}
		
		PackageInclusion existingInclusion = packageInclusionRepository.findById(inclusion.getInclusionId())
	            .orElseThrow(() -> new EntityNotFoundException("Package Inclusion with ID \"" + inclusion.getInclusionId() + "\" not found."));
		

		if (inclusion.getInclusionName() != null && !inclusion.getInclusionName().trim().isBlank()) {
			existingInclusion.setInclusionName(inclusion.getInclusionName().trim());
	    }
		
		if (inclusion.getInclusionPricePerPerson() != null && inclusion.getInclusionPricePerPerson() >= 0) {
			existingInclusion.setInclusionPricePerPerson(inclusion.getInclusionPricePerPerson());
	    }
		
		return packageInclusionRepository.save(existingInclusion);
	}
	
	public void deleteInclusion(int id) {
		PackageInclusion inclusion = packageInclusionRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Package Inclusion with ID \"" + id + "\" not found."));
	    
		packageInclusionRepository.delete(inclusion);
	}
}
