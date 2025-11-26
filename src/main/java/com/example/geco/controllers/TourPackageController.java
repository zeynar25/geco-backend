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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.PackageInclusion;
import com.example.geco.domains.TourPackage;
import com.example.geco.dto.TourPackageRequest;
import com.example.geco.dto.TourPackageUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/package")
@Tag(name = "Tour Package Controller", description = "Manage tour packages, including CRUD operations, soft/hard delete, and restore")
public class TourPackageController extends AbstractController {

    @Operation(
        summary = "Add a new Tour Package",
        description = "Create a new tour package with name, description, base price, and inclusions."
    )
    @PostMapping
    public ResponseEntity<TourPackage> addPackage(
        @Parameter(description = "Tour package object to create") @RequestBody @Valid TourPackageRequest request
    ) {
        TourPackage savedPackage = tourPackageService.addPackage(request);
        return new ResponseEntity<>(savedPackage, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Get Tour Package by ID",
        description = "Retrieve a single tour package by its ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<TourPackage> getPackage(
        @Parameter(description = "ID of the tour package to retrieve") @PathVariable int id
    ) {
        TourPackage tourPackage = tourPackageService.getPackage(id);
        return new ResponseEntity<>(tourPackage, HttpStatus.OK);
    }

    @Operation(
        summary = "Get all Tour Packages",
        description = "Retrieve a list of all tour packages, including active and inactive."
    )
    @GetMapping
    public ResponseEntity<List<TourPackage>> getAllPackages() {
        List<TourPackage> tourPackages = tourPackageService.getAllPackages();
        return new ResponseEntity<>(tourPackages, HttpStatus.OK);
    }

    @Operation(
        summary = "Get all Active Tour Packages",
        description = "Retrieve a list of all active tour packages."
    )
    @GetMapping("/active")
    public ResponseEntity<List<TourPackage>> getAllActivePackages() {
        List<TourPackage> tourPackages = tourPackageService.getAllActivePackages();
        return new ResponseEntity<>(tourPackages, HttpStatus.OK);
    }

    @Operation(
        summary = "Get all Inactive Tour Packages",
        description = "Retrieve a list of all inactive tour packages."
    )
    @GetMapping("/inactive")
    public ResponseEntity<List<TourPackage>> getAllInactivePackages() {
        List<TourPackage> tourPackages = tourPackageService.getAllInactivePackages();
        return new ResponseEntity<>(tourPackages, HttpStatus.OK);
    }
    
    @Operation(
	    summary = "Get active inclusions not in a tour package",
	    description = "Retrieve a list of all active package inclusions that are not already included in the specified tour package."
	)
	@GetMapping("/{id}/inclusions/available")
	public List<PackageInclusion> getAvailableInclusions(@PathVariable Integer id) {
	    return tourPackageService.getAvailableInclusions(id);
	}

    @Operation(
        summary = "Update a Tour Package",
        description = "Update the details of a tour package by ID. Fields that are null or empty will not be updated."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<TourPackage> updatePackage(
        @Parameter(description = "ID of the tour package to update") @PathVariable int id,
        @Parameter(description = "Tour package object with updated fields") @RequestBody @Valid TourPackageUpdateRequest request
    ) {
        TourPackage updatedPackage = tourPackageService.updatePackage(id, request);
        return new ResponseEntity<>(updatedPackage, HttpStatus.OK);
    }

    @Operation(
        summary = "Delete a Tour Package",
        description = "Deletes a tour package by ID. Can perform soft or hard delete based on the 'soft' query parameter."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackage(
        @Parameter(description = "ID of the tour package to delete") @PathVariable int id,
        @Parameter(description = "Soft delete by default; set false for hard delete") @RequestParam(defaultValue = "true") boolean soft
    ) {
        if (soft) {
            tourPackageService.softDeletePackage(id);
        } else {
            tourPackageService.hardDeletePackage(id);
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Restore a Tour Package",
        description = "Restore a previously soft-deleted tour package by ID."
    )
    @PatchMapping("/admin/restore/{id}")
    public ResponseEntity<Void> restorePackage(
        @Parameter(description = "ID of the tour package to restore") @PathVariable int id
    ) {
        tourPackageService.restorePackage(id);
        return ResponseEntity.noContent().build();
    }
}
