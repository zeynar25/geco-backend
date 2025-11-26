package com.example.geco.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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

import com.example.geco.dto.FeedbackRequest;
import com.example.geco.dto.FeedbackResponse;
import com.example.geco.dto.FeedbackUpdateRequest;
import com.example.geco.dto.UserFeedbackUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/feedback")
@Tag(name = "Feedback Controller", description = "Manage feedback for bookings, including CRUD operations, soft delete, restore, and status updates")
public class FeedbackController extends AbstractController {

    @Operation(
        summary = "Add Feedback",
        description = "Create a new feedback for a booking. Requires booking ID, feedback category, stars, comment, and optional suggestion."
    )
    @PostMapping
    public ResponseEntity<FeedbackResponse> addFeedback(
        @Parameter(description = "Feedback details to create") @RequestBody @Valid FeedbackRequest request
    ) {
        FeedbackResponse savedFeedback = feedbackService.addFeedback(request);
        return new ResponseEntity<>(savedFeedback, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Get Feedback by ID",
        description = "Retrieve a single feedback by its unique ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getFeedback(
        @Parameter(description = "ID of the feedback to retrieve") @PathVariable int id
    ) {
        FeedbackResponse feedback = feedbackService.getFeedback(id);
        return new ResponseEntity<>(feedback, HttpStatus.OK);
    }

    @Operation(
        summary = "Get All Feedbacks",
        description = "Retrieve all feedbacks. Can filter by category ID and/or date range."
    )
    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getFeedbacks(
        @Parameter(description = "Filter feedbacks by category ID") @RequestParam(required = false) Integer categoryId,
        @Parameter(description = "Start date for filtering feedbacks") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Parameter(description = "End date for filtering feedbacks") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<FeedbackResponse> feedbacks = feedbackService.getFeedbacks(categoryId, startDate, endDate, null);
        return ResponseEntity.ok(feedbacks);
    }

    @Operation(
        summary = "Get Active Feedbacks",
        description = "Retrieve all feedbacks that are currently active. Can filter by category ID and/or date range."
    )
    @GetMapping("/active")
    public ResponseEntity<List<FeedbackResponse>> getActiveFeedbacks(
        @Parameter(description = "Filter feedbacks by category ID") @RequestParam(required = false) Integer categoryId,
        @Parameter(description = "Start date for filtering feedbacks") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Parameter(description = "End date for filtering feedbacks") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<FeedbackResponse> feedbacks = feedbackService.getFeedbacks(categoryId, startDate, endDate, true);
        return ResponseEntity.ok(feedbacks);
    }

    @Operation(
        summary = "Get Inactive Feedbacks",
        description = "Retrieve all feedbacks that are currently inactive. Can filter by category ID and/or date range."
    )
    @GetMapping("/inactive")
    public ResponseEntity<List<FeedbackResponse>> getInactiveFeedbacks(
        @Parameter(description = "Filter feedbacks by category ID") @RequestParam(required = false) Integer categoryId,
        @Parameter(description = "Start date for filtering feedbacks") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Parameter(description = "End date for filtering feedbacks") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<FeedbackResponse> feedbacks = feedbackService.getFeedbacks(categoryId, startDate, endDate, false);
        return ResponseEntity.ok(feedbacks);
    }

    @Operation(
        summary = "Update Feedback (User)",
        description = "Allows a user to update their feedback. Only certain fields (stars, comment, suggestion) are editable by the user."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<FeedbackResponse> updateFeedback(
        @Parameter(description = "ID of the feedback to update") @PathVariable int id,
        @Parameter(description = "Feedback update details") @RequestBody @Valid UserFeedbackUpdateRequest request
    ) {
        FeedbackResponse updatedFeedback = feedbackService.updateFeedback(id, request);
        return new ResponseEntity<>(updatedFeedback, HttpStatus.OK);
    }

    @Operation(
        summary = "Update Feedback (Staff/Admin)",
        description = "Allows staff or admin to update feedback fields, including stars, comment, suggestion, and feedback status."
    )
    @PatchMapping("/staff/{id}")
    public ResponseEntity<FeedbackResponse> updateFeedbackByStaff(
        @Parameter(description = "ID of the feedback to update") @PathVariable int id,
        @Parameter(description = "Feedback update details") @RequestBody @Valid FeedbackUpdateRequest request
    ) {
        FeedbackResponse updatedFeedback = feedbackService.updateFeedbackByStaff(id, request);
        return new ResponseEntity<>(updatedFeedback, HttpStatus.OK);
    }

    @Operation(
        summary = "Soft Delete Feedback",
        description = "Marks a feedback as inactive (soft delete) without removing it from the database."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteFeedback(
        @Parameter(description = "ID of the feedback to soft delete") @PathVariable int id
    ) {
        feedbackService.softDeleteFeedback(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Restore Feedback",
        description = "Restores a previously soft-deleted feedback."
    )
    @PatchMapping("/restore/{id}")
    public ResponseEntity<Void> restoreFeedback(
        @Parameter(description = "ID of the feedback to restore") @PathVariable int id
    ) {
        feedbackService.restoreFeedback(id);
        return ResponseEntity.noContent().build();
    }
}
