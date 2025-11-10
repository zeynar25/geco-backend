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

import com.example.geco.domains.Feedback;
import com.example.geco.dto.FeedbackResponse;

@RestController
@RequestMapping("/feedback")
public class FeedbackController extends AbstractController {
	@PostMapping
	public ResponseEntity<?> addFeedback(@RequestBody Feedback feedback) {
		FeedbackResponse savedFeedback = feedbackService.addFeedback(feedback);
		return new ResponseEntity<>(savedFeedback, HttpStatus.CREATED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getFeedback(@PathVariable int id) {
		FeedbackResponse savedfeedback = feedbackService.getFeedback(id);
		return new ResponseEntity<>(savedfeedback, HttpStatus.OK);
	}
	
	@GetMapping("/{categoryId}/{year}/{month}")
	public ResponseEntity<?> getFeedbackByCategory(
			@PathVariable int categoryId, 
			@PathVariable int year, 
			@PathVariable int month) {
		
		List<FeedbackResponse> feedbackResponses = feedbackService.getFeedbackByCategoryNameAndDate(categoryId, year, month);

	    return new ResponseEntity<>(feedbackResponses, HttpStatus.OK);
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<?> updateFeedback(@PathVariable int id, @RequestBody Feedback feedback) {
		feedback.setFeedbackId(id);
		FeedbackResponse savedfeedback = feedbackService.getFeedback(id);
		return new ResponseEntity<>(savedfeedback, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteFeedback(@PathVariable int id) {
		FeedbackResponse deletedFeedback = feedbackService.getFeedback(id);
		return new ResponseEntity<>(deletedFeedback, HttpStatus.OK);
	}
}
