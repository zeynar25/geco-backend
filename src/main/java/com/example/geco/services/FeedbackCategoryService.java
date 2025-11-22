package com.example.geco.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.geco.domains.FeedbackCategory;
import com.example.geco.repositories.FeedbackCategoryRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class FeedbackCategoryService {
	@Autowired
	private FeedbackCategoryRepository feedbackCategoryRepository;
	
	public void validateCategory(FeedbackCategory category) {
		if (category.getLabel() == null || category.getLabel().trim().isEmpty()) {
	        throw new IllegalArgumentException("Label must at least have 1 character.");
	    }
	}
	
	public FeedbackCategory addCategory(FeedbackCategory category) {
		validateCategory(category);
		
		String label = category.getLabel();
		
		// Check if this category label already exist.
		if (feedbackCategoryRepository.existsByLabelIgnoreCase(label)) {
			throw new IllegalArgumentException("Label \"" + label + "\" already exist.");
		}
		
		return feedbackCategoryRepository.save(category);
	}
	
	public FeedbackCategory getCategory(int id) {
		return feedbackCategoryRepository.findById(id)
				.orElseThrow(
	            		() -> new EntityNotFoundException("Feedback Category with ID " + id + " not found.")
	            );
	}
	
	public List<FeedbackCategory> getAllCategories() {
		return feedbackCategoryRepository.findAllByOrderByLabel();
	}
	
	public FeedbackCategory updateCategory(FeedbackCategory category) {
		FeedbackCategory existingCategory = feedbackCategoryRepository.findById(
				category.getFeedbackCategoryId()
		).orElseThrow(
				() -> new EntityNotFoundException("Feedback category \""+ category.getLabel() +"\" not found.")
		);
		
		validateCategory(category);
		String label = category.getLabel();
		
		// Check if this category label already exist.
		if (feedbackCategoryRepository.existsByLabelIgnoreCase(label)) {
			throw new IllegalArgumentException("Label \"" + label + "\" already exist.");
		}
		
		existingCategory.setLabel(category.getLabel());
		
		return feedbackCategoryRepository.save(existingCategory);
	}
	
	public void deleteCategory(int id) {
		FeedbackCategory existingCategory = feedbackCategoryRepository.findById(
				id
		).orElseThrow(
				() -> new EntityNotFoundException("Feedback category with ID " + id + " not found.")
		);
		
		feedbackCategoryRepository.delete(existingCategory);
	}
}
