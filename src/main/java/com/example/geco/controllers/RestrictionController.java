package com.example.geco.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.Restriction;
import com.example.geco.dto.FeedbackResponse;
import com.example.geco.dto.RestrictionRequest;
import com.example.geco.dto.RestrictionUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/restriction")
@Tag(name = "Restriction Controller", description = "Manage restrictions, including CRU operations")
public class RestrictionController extends AbstractController{
	 @Operation(
        summary = "Add a new Restriction",
        description = "Create a new restriction with name, and value."
    )
    @PostMapping
    public ResponseEntity<Restriction> addRestriction(
        @Parameter(description = "Restriction object to create") @RequestBody @Valid RestrictionRequest request
    ) {
        Restriction savedRestriction = restrictionService.addRestriction(request);
        return new ResponseEntity<>(savedRestriction, HttpStatus.CREATED);
    }
	 
	@Operation(
        summary = "Get Restriction by ID",
        description = "Retrieve a single restriction by its unique ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<Restriction> getRestriction(
        @Parameter(description = "ID of the restriction to retrieve")
        @PathVariable int id
    ) {
        Restriction restriction = restrictionService.getRestriction(id);
        return new ResponseEntity<>(restriction, HttpStatus.OK);
    }
	 
	@Operation(
       summary = "Get Restriction by name",
       description = "Retrieve a single restriction by its name."
   )
   @GetMapping("/name/{name}")
   public ResponseEntity<Restriction> getRestrictionByName(
       @Parameter(description = "Name of the restriction to retrieve")
       @PathVariable String name
   ) {
       Restriction restriction = restrictionService.getRestriction(name);
       return new ResponseEntity<>(restriction, HttpStatus.OK);
   }
	 
	@Operation(
        summary = "Update a Restriction",
        description = "Update the details of a restriction by ID. Fields that are null or empty will not be updated."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Restriction> updateRestriction(
        @Parameter(description = "ID of the restriction to update") @PathVariable int id,
        @Parameter(description = "Restriction object with updated fields") @RequestBody @Valid RestrictionUpdateRequest request
    ) {
        Restriction updatedRestriction = restrictionService.updateRestriction(id, request);
        return new ResponseEntity<>(updatedRestriction, HttpStatus.OK);
    }
}
