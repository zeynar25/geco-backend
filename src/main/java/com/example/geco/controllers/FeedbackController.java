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

import com.example.geco.domains.Feedback;
import com.example.geco.dto.FeedbackResponse;

@RestController
@RequestMapping("/feedback")
public class FeedbackController extends AbstractController {
	@PostMapping
	public ResponseEntity<FeedbackResponse> addFeedback(@RequestBody Feedback feedback) {
		FeedbackResponse savedFeedback = feedbackService.addFeedback(feedback);
		return new ResponseEntity<>(savedFeedback, HttpStatus.CREATED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<FeedbackResponse> getFeedback(@PathVariable int id) {
		FeedbackResponse feedback = feedbackService.getFeedback(id);
		return new ResponseEntity<>(feedback, HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<List<FeedbackResponse>> getFeedbackByCategory(
			@RequestParam(required = false) Integer categoryId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		
		List<FeedbackResponse> feedbacks = feedbackService.getFeedbackByCategoryAndDateRange(categoryId, startDate, endDate);

	    return new ResponseEntity<>(feedbacks, HttpStatus.OK);
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<FeedbackResponse> updateFeedback(@PathVariable int id, @RequestBody Feedback feedback) {
		feedback.setFeedbackId(id);
		FeedbackResponse updatedFeedback = feedbackService.updateFeedback(feedback);
		return new ResponseEntity<>(updatedFeedback, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<FeedbackResponse> deleteFeedback(@PathVariable int id) {
		feedbackService.deleteFeedback(id);
	    return ResponseEntity.noContent().build();
	}
}
