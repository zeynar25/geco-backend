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
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.FeedbackCategory;

@RestController
public class FeedbackCategoryController extends AbstractController {
	@PostMapping("/feedback-category")
	public ResponseEntity<?> addFeedbackCategory(@RequestBody FeedbackCategory category) {
		FeedbackCategory savedCategory = feedbackCategoryService.addCategory(category);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
	}
	
	@GetMapping("/feedback-category/{id}")
	public ResponseEntity<?> getFeedbackCategory(@PathVariable int id) {
		FeedbackCategory savedCategory = feedbackCategoryService.getCategory(id);
        return new ResponseEntity<>(savedCategory, HttpStatus.OK);
	}
	
	@GetMapping("/feedback-category")
	public ResponseEntity<List<FeedbackCategory>> getAllFeedbackCategories() {
		List<FeedbackCategory> categories = feedbackCategoryService.getAllCategories();
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}
	
	@PatchMapping("/feedback-category/{id}")
	public ResponseEntity<?> updateFeedbackCategory(@PathVariable int id, @RequestBody FeedbackCategory category) {
		category.setFeedbackCategoryId(id);
		FeedbackCategory savedCategory = feedbackCategoryService.updateCategory(category);
        return new ResponseEntity<>(savedCategory, HttpStatus.OK);
	}

	@DeleteMapping("/feedback-category/{id}")
	public ResponseEntity<?> deleteFeedbackCategory(@PathVariable int id) {
		FeedbackCategory savedCategory = feedbackCategoryService.deleteCategory(id);
        return new ResponseEntity<>(savedCategory, HttpStatus.OK);
	}
}
