package com.example.geco.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.PackageInclusion;

@RestController
@RequestMapping("/package-inclusion")
public class PackageInclusionController extends AbstractController {
	@PostMapping
	public ResponseEntity<PackageInclusion> addInclusion(@RequestBody PackageInclusion inclusion) {
		PackageInclusion savedInclusion = packageInclusionService.addInclusion(inclusion);
        return new ResponseEntity<>(savedInclusion, HttpStatus.CREATED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<PackageInclusion> getInclusion(@PathVariable int id) {
		PackageInclusion inclusion = packageInclusionService.getInclusion(id);
        return new ResponseEntity<>(inclusion, HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<List<PackageInclusion>> getAllInclusions() {
		List<PackageInclusion> inclusions = packageInclusionService.getAllInclusions();
        return new ResponseEntity<>(inclusions, HttpStatus.OK);
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<PackageInclusion> updateInclusion(@PathVariable int id, @RequestBody PackageInclusion inclusion) {
		inclusion.setInclusionId(id);
		PackageInclusion updatedInclusion = packageInclusionService.updateInclusion(inclusion);
        return new ResponseEntity<>(updatedInclusion, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<PackageInclusion> deleteInclusion(@PathVariable int id) {
		PackageInclusion deletedPackage = packageInclusionService.deleteInclusion(id);
        return new ResponseEntity<>(deletedPackage, HttpStatus.OK);
	}
}
