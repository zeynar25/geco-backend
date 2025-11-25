package com.example.geco.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.geco.domains.FeedbackCategory;
import com.example.geco.domains.AuditLog.LogAction;
import com.example.geco.dto.FeedbackCategoryRequest;
import com.example.geco.repositories.FeedbackCategoryRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class FeedbackCategoryService extends BaseService{
	@Autowired
	private FeedbackCategoryRepository feedbackCategoryRepository;

	private FeedbackCategory createCategoryCopy(FeedbackCategory category) {
		return FeedbackCategory.builder()
				.feedbackCategoryId(category.getFeedbackCategoryId())
				.label(category.getLabel())
				.isActive(category.isActive())
				.build();
	}
	
	@Transactional
	public FeedbackCategory addCategory(FeedbackCategoryRequest category) {
		String label = category.getLabel().trim();
		
		// Check if this category label already exist.
		if (feedbackCategoryRepository.existsByLabelIgnoreCase(label)) {
			throw new IllegalArgumentException("Label '" + label + "' already exist.");
		}

		FeedbackCategory savedCategory = feedbackCategoryRepository.save(
				FeedbackCategory.builder()
					.label(label)
					.build());
		
		logIfStaffOrAdmin("FeedbackCategory", (long) savedCategory.getFeedbackCategoryId(), LogAction.CREATE, null, savedCategory);
		
		return savedCategory;
	}
	
	
	public FeedbackCategory getCategory(int id) {
		return feedbackCategoryRepository.findById(id)
				.orElseThrow(
	            		() -> new EntityNotFoundException("Feedback category with ID '" + id + "' not found.")
	            );
	}
	
	public List<FeedbackCategory> getAllCategories() {
		return feedbackCategoryRepository.findAllByOrderByLabel();
	}
	
	public List<FeedbackCategory> getAllActiveCategories() {
		return feedbackCategoryRepository.findAllByIsActiveOrderByLabel(true);
	}
	
	public List<FeedbackCategory> getAllInactiveCategories() {
		return feedbackCategoryRepository.findAllByIsActiveOrderByLabel(false);
	}
	
	@Transactional
	public FeedbackCategory updateCategory(int id, FeedbackCategoryRequest category) {
		FeedbackCategory existingCategory = feedbackCategoryRepository.findById(id)
				.orElseThrow(
						() -> new EntityNotFoundException("Feedback category with ID '" + id + "' not found.")
		);

		FeedbackCategory prevCategory  = createCategoryCopy(existingCategory);
		String newLabel = category.getLabel().trim();
		
		if (existingCategory.getLabel().equalsIgnoreCase(newLabel)) {
		    return existingCategory;
		}
		
		if (feedbackCategoryRepository.existsByLabelIgnoreCaseAndFeedbackCategoryIdNot(newLabel, id)) {
			throw new IllegalArgumentException("Label '" + newLabel + "' already exist.");
		}
		
		existingCategory.setLabel(newLabel);
		
		logIfStaffOrAdmin("FeedbackCategory", (long) id, LogAction.UPDATE, prevCategory, existingCategory);
		
		return feedbackCategoryRepository.save(existingCategory);
	}
	
	@Transactional
	public void softDeleteCategory(int id) {
		FeedbackCategory category = feedbackCategoryRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Feedback category with ID '" + id + "' not found."));
	    
		FeedbackCategory prevCategory  = createCategoryCopy(category);
		
		if (!category.isActive()) {
	        throw new IllegalStateException("Category " + category.getLabel() + " is already disabled.");
	    }

		category.setActive(false);
		feedbackCategoryRepository.save(category);
		
		logIfStaffOrAdmin("FeedbackCategory", (long) id, LogAction.DISABLE, prevCategory, category);
	}

	@Transactional
	public void restoreCategory(int id) {
		FeedbackCategory category = feedbackCategoryRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Feedback category with ID '" + id + "' not found."));
	    
		FeedbackCategory prevCategory  = createCategoryCopy(category);

		if (category.isActive()) {
	        throw new IllegalStateException("Category " + category.getLabel() + " is already active.");
	    }
		
		category.setActive(true);
		feedbackCategoryRepository.save(category);
		
		logIfStaffOrAdmin("FeedbackCategory", (long) id, LogAction.RESTORE, prevCategory, category);
	}
}
