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

import com.example.geco.domains.Attraction;
import com.example.geco.dto.AttractionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/attraction")
@Tag(name = "Attraction", description = "Operations about attractions")
public class AttractionController extends AbstractController {

    @PostMapping
    @Operation(
        summary = "Add a new attraction",
        description = "Creates a new attraction with the provided details"
    )
    public ResponseEntity<AttractionResponse> addAttraction(
            @RequestBody @Valid Attraction attraction) {
        AttractionResponse savedAttraction = attractionService.addAttraction(attraction);
        return new ResponseEntity<>(savedAttraction, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get attraction by ID",
        description = "Retrieves a single attraction by its ID"
    )
    public ResponseEntity<AttractionResponse> getAttraction(@PathVariable int id) {
        AttractionResponse attraction = attractionService.getAttraction(id);
        return new ResponseEntity<>(attraction, HttpStatus.OK);
    }

    @GetMapping
    @Operation(
        summary = "Get all attractions",
        description = "Retrieves a list of all attractions, regardless of active status"
    )
    public ResponseEntity<List<AttractionResponse>> getAllAttractions() {
        List<AttractionResponse> attractions = attractionService.getAllAttractions();
        return new ResponseEntity<>(attractions, HttpStatus.OK);
    }

    @GetMapping("/active")
    @Operation(
        summary = "Get all active attractions",
        description = "Retrieves a list of all attractions that are currently active"
    )
    public ResponseEntity<List<AttractionResponse>> getAllActiveAttractions() {
        List<AttractionResponse> attractions = attractionService.getAllActiveAttractions();
        return new ResponseEntity<>(attractions, HttpStatus.OK);
    }

    @GetMapping("/inactive")
    @Operation(
        summary = "Get all inactive attractions",
        description = "Retrieves a list of all attractions that are currently inactive"
    )
    public ResponseEntity<List<AttractionResponse>> getAllInactiveAttractions() {
        List<AttractionResponse> attractions = attractionService.getAllInactiveAttractions();
        return new ResponseEntity<>(attractions, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Update attraction by ID",
        description = "Updates the name and/or description of an attraction by its ID"
    )
    public ResponseEntity<AttractionResponse> updateAttraction(
            @PathVariable int id, 
            @RequestBody @Valid Attraction attraction) {
        AttractionResponse updatedAttraction = attractionService.updateAttraction(id, attraction);
        return new ResponseEntity<>(updatedAttraction, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete an attraction",
        description = "Deletes an attraction by ID. Can perform soft or hard delete based on the 'soft' query parameter"
    )
    public ResponseEntity<Void> deleteAttraction(
            @PathVariable int id,
            @RequestParam(defaultValue = "true") boolean soft) {
        if (soft) {
            attractionService.softDeleteAttraction(id);
        } else {
            attractionService.hardDeleteAttraction(id);
        }
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/restore/{id}")
    @Operation(
        summary = "Restore an attraction",
        description = "Restore an attraction by ID."
    )
    public ResponseEntity<Void> restoreAttraction(
            @PathVariable int id,
            @RequestParam(defaultValue = "true") boolean soft) {

        attractionService.restoreAttraction(id);
        return ResponseEntity.noContent().build();
    }
}
