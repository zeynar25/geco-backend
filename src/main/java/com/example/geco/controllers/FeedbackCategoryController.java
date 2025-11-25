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

import com.example.geco.domains.FeedbackCategory;
import com.example.geco.dto.FeedbackCategoryRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/feedback-category")
@Tag(name = "Feedback Category", description = "Operations about feedback categories")
public class FeedbackCategoryController extends AbstractController {

    @PostMapping
    @Operation(
        summary = "Add a new feedback category",
        description = "Creates a new feedback category with the provided label"
    )
    public ResponseEntity<FeedbackCategory> addFeedbackCategory(
            @RequestBody @Valid FeedbackCategoryRequest request) {
        FeedbackCategory savedCategory = feedbackCategoryService.addCategory(request);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get feedback category by ID",
        description = "Retrieves a single feedback category by its ID"
    )
    public ResponseEntity<FeedbackCategory> getFeedbackCategory(@PathVariable int id) {
        FeedbackCategory category = feedbackCategoryService.getCategory(id);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @GetMapping
    @Operation(
        summary = "Get all feedback categories",
        description = "Retrieves a list of all feedback categories, regardless of active status"
    )
    public ResponseEntity<List<FeedbackCategory>> getAllFeedbackCategories() {
        List<FeedbackCategory> categories = feedbackCategoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/active")
    @Operation(
        summary = "Get all active feedback categories",
        description = "Retrieves a list of all feedback categories that are currently active"
    )
    public ResponseEntity<List<FeedbackCategory>> getAllActiveFeedbackCategories() {
        List<FeedbackCategory> categories = feedbackCategoryService.getAllActiveCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/inactive")
    @Operation(
        summary = "Get all inactive feedback categories",
        description = "Retrieves a list of all feedback categories that are currently inactive"
    )
    public ResponseEntity<List<FeedbackCategory>> getAllInactiveFeedbackCategories() {
        List<FeedbackCategory> categories = feedbackCategoryService.getAllInactiveCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Update feedback category by ID",
        description = "Updates the label of a feedback category by its ID. No log or update occurs if the label is unchanged."
    )
    public ResponseEntity<FeedbackCategory> updateFeedbackCategory(
            @PathVariable int id,
            @RequestBody @Valid FeedbackCategoryRequest request) {
        FeedbackCategory updatedCategory = feedbackCategoryService.updateCategory(id, request);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Soft delete a feedback category",
        description = "Disables a feedback category by ID without permanently deleting it"
    )
    public ResponseEntity<Void> deleteFeedbackCategory(@PathVariable int id) {
        feedbackCategoryService.softDeleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/admin/restore/{id}")
    @Operation(
        summary = "Restore a feedback category",
        description = "Restores a previously disabled feedback category by ID"
    )
    public ResponseEntity<Void> restoreFeedbackCategory(@PathVariable int id) {
        feedbackCategoryService.restoreCategory(id);
        return ResponseEntity.noContent().build();
    }
}
