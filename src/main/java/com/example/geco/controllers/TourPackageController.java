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

import com.example.geco.domains.Faq;
import com.example.geco.domains.TourPackage;

@RestController
@RequestMapping("/package")
public class TourPackageController extends AbstractController{
	@PostMapping
	public ResponseEntity<TourPackage> addPackage(@RequestBody TourPackage tourPackage) {
		TourPackage savedPackage = tourPackageService.addPackage(tourPackage);
        return new ResponseEntity<>(savedPackage, HttpStatus.CREATED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<TourPackage> getPackage(@PathVariable int id) {
		TourPackage tourPackage = tourPackageService.getPackage(id);
        return new ResponseEntity<>(tourPackage, HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<List<TourPackage>> getAllPackages() {
		List<TourPackage> tourPackages = tourPackageService.getAllPackages();
        return new ResponseEntity<>(tourPackages, HttpStatus.OK);
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<TourPackage> updatePackage(@PathVariable int id, @RequestBody TourPackage tourPackage) {
		tourPackage.setPackageId(id);
		TourPackage updatedPackage = tourPackageService.updatePackage(tourPackage);
        return new ResponseEntity<>(updatedPackage, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<TourPackage> deletePackage(@PathVariable int id) {
		tourPackageService.deletePackage(id);
	    return ResponseEntity.noContent().build();
	}
}
