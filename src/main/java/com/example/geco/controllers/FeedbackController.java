package com.example.geco.controllers;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.example.geco.domains.Feedback.FeedbackStatus;
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
@Tag(
    name = "Feedback Controller",
    description = "Manage feedback for bookings, including CRUD operations, soft delete, restore, and status updates"
)
public class FeedbackController extends AbstractController {

    // =========================
    // CREATE & READ SINGLE
    // =========================

    @Operation(
        summary = "Add Feedback",
        description = "Create a new feedback for a booking. Requires booking ID, feedback category, stars, comment, and optional suggestion."
    )
    @PostMapping
    public ResponseEntity<FeedbackResponse> addFeedback(
        @Parameter(description = "Feedback details to create")
        @RequestBody @Valid FeedbackRequest request
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
        @Parameter(description = "ID of the feedback to retrieve")
        @PathVariable int id
    ) {
        FeedbackResponse feedback = feedbackService.getFeedback(id);
        return new ResponseEntity<>(feedback, HttpStatus.OK);
    }

    
    // =========================
    // LOGGED-IN USER FEEDBACKS
    // =========================

    @Operation(
        summary = "Get my feedbacks",
        description = "Retrieve feedbacks submitted by the logged-in user. Optional filters: categoryId, startDate, endDate, stars. Pagination supported."
    )
    @GetMapping("/me")
    public ResponseEntity<Page<FeedbackResponse>> getMyFeedbacks(
        @Parameter(description = "Filter feedbacks by category ID")
        @RequestParam(required = false) Integer categoryId,

        @Parameter(description = "Start date for filtering feedbacks (ISO date)")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

        @Parameter(description = "End date for filtering feedbacks (ISO date)")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

        @Parameter(description = "Filter feedbacks by rounded stars (1-5). Returns ratings >= stars and < stars+1")
        @RequestParam(required = false) Integer stars,

        @Parameter(description = "Page number (0-based index)", example = "0")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Number of records per page", example = "10")
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FeedbackResponse> feedbacks =
            feedbackService.getMyFeedbacks(categoryId, startDate, endDate, stars, pageable);
        return ResponseEntity.ok(feedbacks);
    }

    @Operation(
        summary = "Get all feedbacks",
        description = "Retrieve all feedbacks. Optional filters: categoryId, startDate, endDate, feedbackStatus, isActive, email, stars. Pagination supported."
    )
    @GetMapping
    public ResponseEntity<Page<FeedbackResponse>> getFeedbacks(
        @Parameter(description = "Filter feedbacks by category ID")
        @RequestParam(required = false) Integer categoryId,

        @Parameter(description = "Start date for filtering feedbacks (ISO date)")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

        @Parameter(description = "End date for filtering feedbacks (ISO date)")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

        @Parameter(description = "Filter feedbacks by feedbackStatus")
        @RequestParam(required = false) FeedbackStatus feedbackStatus,

        @Parameter(description = "Filter feedbacks by activeness")
        @RequestParam(required = false) Boolean isActive,

        @Parameter(description = "Filter feedbacks by account email (contains, case-insensitive)")
        @RequestParam(required = false) String email,

        @Parameter(description = "Filter feedbacks by rounded stars (1-5). Returns ratings >= stars and < stars+1")
        @RequestParam(required = false) Integer stars,

        @Parameter(description = "Page number (0-based index)", example = "0")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Number of records per page", example = "10")
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FeedbackResponse> feedbacks =
            feedbackService.getFeedbacks(
                categoryId, startDate, endDate, feedbackStatus, isActive, email, stars, pageable
            );
        return ResponseEntity.ok(feedbacks);
    }

    @Operation(
        summary = "Get active feedbacks",
        description = "Retrieve active feedbacks. Optional filters: categoryId, startDate, endDate, feedbackStatus, email, stars. Pagination supported."
    )
    @GetMapping("/active")
    public ResponseEntity<Page<FeedbackResponse>> getActiveFeedbacks(
        @Parameter(description = "Filter feedbacks by category ID")
        @RequestParam(required = false) Integer categoryId,

        @Parameter(description = "Start date for filtering feedbacks (ISO date)")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

        @Parameter(description = "End date for filtering feedbacks (ISO date)")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

        @Parameter(description = "Filter feedbacks by feedbackStatus")
        @RequestParam(required = false) FeedbackStatus feedbackStatus,

        @Parameter(description = "Filter feedbacks by account email (contains, case-insensitive)")
        @RequestParam(required = false) String email,

        @Parameter(description = "Filter feedbacks by rounded stars (1-5). Returns ratings >= stars and < stars+1")
        @RequestParam(required = false) Integer stars,

        @Parameter(description = "Page number (0-based index)", example = "0")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Number of records per page", example = "10")
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FeedbackResponse> feedbacks =
            feedbackService.getFeedbacks(categoryId, startDate, endDate, feedbackStatus, true, email, stars, pageable);
        return ResponseEntity.ok(feedbacks);
    }

    @Operation(
        summary = "Get inactive feedbacks",
        description = "Retrieve inactive feedbacks. Optional filters: categoryId, startDate, endDate, feedbackStatus, email, stars. Pagination supported."
    )
    @GetMapping("/inactive")
    public ResponseEntity<Page<FeedbackResponse>> getInactiveFeedbacks(
        @Parameter(description = "Filter feedbacks by category ID")
        @RequestParam(required = false) Integer categoryId,

        @Parameter(description = "Start date for filtering feedbacks (ISO date)")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

        @Parameter(description = "End date for filtering feedbacks (ISO date)")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

        @Parameter(description = "Filter feedbacks by feedbackStatus")
        @RequestParam(required = false) FeedbackStatus feedbackStatus,

        @Parameter(description = "Filter feedbacks by account email (contains, case-insensitive)")
        @RequestParam(required = false) String email,

        @Parameter(description = "Filter feedbacks by rounded stars (1-5). Returns ratings >= stars and < stars+1")
        @RequestParam(required = false) Integer stars,

        @Parameter(description = "Page number (0-based index)", example = "0")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Number of records per page", example = "10")
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FeedbackResponse> feedbacks =
            feedbackService.getFeedbacks(categoryId, startDate, endDate, feedbackStatus, false, email, stars, pageable);
        return ResponseEntity.ok(feedbacks);
    }

    // =========================
    // UPDATE
    // =========================

    @Operation(
        summary = "Update Feedback (User)",
        description = "Allows a user to update their feedback. Only certain fields (stars, comment, suggestion) are editable by the user."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<FeedbackResponse> updateFeedback(
        @Parameter(description = "ID of the feedback to update")
        @PathVariable int id,

        @Parameter(description = "Feedback update details")
        @RequestBody @Valid UserFeedbackUpdateRequest request
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
        @Parameter(description = "ID of the feedback to update")
        @PathVariable int id,

        @Parameter(description = "Feedback update details")
        @RequestBody @Valid FeedbackUpdateRequest request
    ) {
        FeedbackResponse updatedFeedback = feedbackService.updateFeedbackByStaff(id, request);
        return new ResponseEntity<>(updatedFeedback, HttpStatus.OK);
    }

    // =========================
    // SOFT DELETE / RESTORE
    // =========================

    @Operation(
        summary = "Soft Delete Feedback",
        description = "Marks a feedback as inactive (soft delete) without removing it from the database."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteFeedback(
        @Parameter(description = "ID of the feedback to soft delete")
        @PathVariable int id
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
        @Parameter(description = "ID of the feedback to restore")
        @PathVariable int id
    ) {
        feedbackService.restoreFeedback(id);
        return ResponseEntity.noContent().build();
    }
}