package com.example.geco.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.geco.domains.PackageInclusion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/package-inclusion")
@Tag(name = "Package Inclusion Controller", description = "Manage package inclusions, including adding, updating, restoring, and deleting")
public class PackageInclusionController extends AbstractController {

    @Operation(summary = "Add a new inclusion", description = "Create a new package inclusion with name and price per person")
    @PostMapping("/staff")
    public ResponseEntity<PackageInclusion> addInclusion(
            @Parameter(description = "PackageInclusion object to create") @RequestBody PackageInclusion inclusion) {
        PackageInclusion savedInclusion = packageInclusionService.addInclusion(inclusion);
        return new ResponseEntity<>(savedInclusion, HttpStatus.CREATED);
    }

    @Operation(summary = "Get inclusion by ID", description = "Retrieve a single package inclusion by its ID")
    @GetMapping("/staff/{id}")
    public ResponseEntity<PackageInclusion> getInclusion(
            @Parameter(description = "ID of the package inclusion to retrieve") @PathVariable int id) {
        PackageInclusion inclusion = packageInclusionService.getInclusion(id);
        return new ResponseEntity<>(inclusion, HttpStatus.OK);
    }

    @Operation(summary = "Get all inclusions", description = "Retrieve a list of all package inclusions")
    @GetMapping("/staff")
    public ResponseEntity<List<PackageInclusion>> getAllInclusions() {
        List<PackageInclusion> inclusions = packageInclusionService.getAllInclusions();
        return new ResponseEntity<>(inclusions, HttpStatus.OK);
    }

    @Operation(summary = "Get all active inclusions", description = "Retrieve a list of all active package inclusions")
    @GetMapping("/active")
    public ResponseEntity<List<PackageInclusion>> getAllActiveInclusions() {
        List<PackageInclusion> inclusions = packageInclusionService.getAllActiveInclusions();
        return new ResponseEntity<>(inclusions, HttpStatus.OK);
    }

    @Operation(summary = "Get all inactive inclusions", description = "Retrieve a list of all inactive package inclusions")
    @GetMapping("/staff/inactive")
    public ResponseEntity<List<PackageInclusion>> getAllInactiveInclusions() {
        List<PackageInclusion> inclusions = packageInclusionService.getAllInactiveInclusions();
        return new ResponseEntity<>(inclusions, HttpStatus.OK);
    }

    @Operation(summary = "Update inclusion by ID", description = "Update name and/or price of a package inclusion by its ID")
    @PatchMapping("/staff/{id}")
    public ResponseEntity<PackageInclusion> updateInclusion(
            @Parameter(description = "ID of the inclusion to update") @PathVariable int id,
            @RequestBody PackageInclusion inclusion) {
        PackageInclusion updatedInclusion = packageInclusionService.updateInclusion(id, inclusion);
        return new ResponseEntity<>(updatedInclusion, HttpStatus.OK);
    }

    @Operation(summary = "Soft delete inclusion by ID", description = "Mark a package inclusion as inactive")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteInclusion(
            @Parameter(description = "ID of the inclusion to soft delete") @PathVariable int id) {
        packageInclusionService.softDeleteInclusion(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Restore inclusion by ID", description = "Restore a previously soft-deleted package inclusion")
    @PatchMapping("/admin/restore/{id}")
    public ResponseEntity<Void> restoreInclusion(
            @Parameter(description = "ID of the inclusion to restore") @PathVariable int id) {
        packageInclusionService.restoreInclusion(id);
        return ResponseEntity.noContent().build();
    }
}
