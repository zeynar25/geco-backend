package com.example.geco.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.geco.domains.Feedback;
import com.example.geco.domains.Feedback.FeedbackStatus;
import com.example.geco.domains.FeedbackCategory;
import com.example.geco.domains.Booking.BookingStatus;
import com.example.geco.dto.FeedbackResponse;
import com.example.geco.repositories.AccountRepository;
import com.example.geco.repositories.BookingRepository;
import com.example.geco.repositories.FeedbackCategoryRepository;
import com.example.geco.repositories.FeedbackRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class FeedbackService {
	@Autowired
	private FeedbackCategoryRepository feedbackCategoryRepository;
	
	@Autowired
	private FeedbackRepository feedbackRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	public FeedbackResponse toResponse(Feedback feedback) {
		return new FeedbackResponse(
				feedback.getFeedbackId(),
				feedback.getAccount(),
				feedback.getBooking(),
				feedback.getCategory().getLabel(),
				feedback.getStars(),
				feedback.getComment(),
				feedback.getSuggestion(),
				feedback.getStatus()
		);
	}
	
	public FeedbackResponse addFeedback(Feedback feedback) {
		if (feedback.getAccount() == null || feedback.getAccount().getAccountId() == null) {
	        throw new IllegalArgumentException("Account is missing or invalid.");
	    }
		
	    accountRepository.findById(
	    		feedback.getAccount().getAccountId()
	    ).orElseThrow(
	    		() -> new EntityNotFoundException("Account not found.")
		);

	    if (feedback.getBooking() == null || feedback.getBooking().getBookingId() == null) {
	        throw new IllegalArgumentException("Booking is missing or invalid.");
	    }

	    bookingRepository.findById(
	    		feedback.getBooking().getBookingId()
	    ).orElseThrow(
	    		() -> new EntityNotFoundException("Booking not found.")
	    );
	    
	    if (feedback.getCategory() == null || feedback.getCategory().getFeedbackCategoryId() == null) {
	        throw new IllegalArgumentException("Category is missing or invalid.");
	    }
	    
	    if (feedback.getStars() == null || feedback.getStars() < 0 || feedback.getStars() > 5) {
	        throw new IllegalArgumentException("Stars must be between 0 and 5.");
	    }
		
		if (feedback.getComment() == null || feedback.getComment().trim().length() < 10) {
		    throw new IllegalArgumentException("Feedback comment must be at least 10 characters long.");
		}
		
		if (feedback.getSuggestion() == null || feedback.getSuggestion().trim().length() < 10) {
		    throw new IllegalArgumentException("Suggestion comment must be at least 10 characters long.");
		}
		
		// Check if the account's feedback for this booking already exist.
		if (feedbackRepository.existsByBooking_BookingIdAndAccount_AccountId(
		        feedback.getBooking().getBookingId(),
		        feedback.getAccount().getAccountId()
		)) {
		    throw new IllegalArgumentException("Feedback for this booking by this account already exists.");
		}
		
		FeedbackCategory existingCategory = feedbackCategoryRepository.findById(
		        feedback.getCategory().getFeedbackCategoryId())
				.orElseThrow(() -> new EntityNotFoundException("Category not found."));
		
		feedback.setCategory(existingCategory);
		feedback.setStatus(FeedbackStatus.NEW);
	    
	    return toResponse(feedbackRepository.save(feedback));
	}
	
	public FeedbackResponse getFeedback(int id) {
		Feedback feedback = feedbackRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Feedback with ID \"" + id + "\" not found."));
	    
	    return toResponse(feedback);
	}
	
	public List<FeedbackResponse> getFeedbackByCategoryAndDateRange(
	        Integer categoryId, 
	        LocalDate startDate, 
	        LocalDate endDate) {

	    List<Feedback> feedbacks;

	    if (categoryId == null && startDate == null && endDate == null) {
	        feedbacks = feedbackRepository.findAll();
	    } else if (categoryId == null) {
	        feedbacks = feedbackRepository.findByBooking_VisitDateBetween(startDate, endDate);
	    } else if (startDate == null && endDate == null) {
	        feedbacks = feedbackRepository.findByCategory_FeedbackCategoryId(categoryId);
	    } else {
	        feedbacks = feedbackRepository.findByCategory_FeedbackCategoryIdAndBooking_VisitDateBetween(
	                categoryId, startDate, endDate);
	    }

	    return feedbacks.stream().map(this::toResponse).toList();
	}

	
	public FeedbackResponse updateFeedback(Feedback feedback) {
		if (feedback.getAccount() == null && 
				feedback.getBooking() == null && 
				feedback.getCategory() == null && 
				feedback.getStars() == null && 
				feedback.getComment() == null &&
				feedback.getSuggestion() == null &&
				feedback.getStatus() == null) {
			throw new IllegalArgumentException("No fields provided to update feedback.");
		}
		
		Feedback existingFeedback = feedbackRepository.findById(feedback.getFeedbackId())
				.orElseThrow(() -> new EntityNotFoundException("Feedback with ID \"" + feedback.getFeedbackId() + "\" not found."));
		
		if (feedback.getCategory() != null) {
			FeedbackCategory existingCategory = feedbackCategoryRepository.findById(
			        feedback.getCategory().getFeedbackCategoryId())
					.orElseThrow(() -> new EntityNotFoundException("Category not found."));
			existingFeedback.setCategory(existingCategory);
		}
		
		if (feedback.getStars() != null) {
			// Round getStars to one decimal.
			Double stars = Math.round(feedback.getStars() * 10.0) / 10.0;
			
			if (stars >= 0 && stars <= 5.0) {
				existingFeedback.setStars(stars); 
			} 
	    } 
		
		if (feedback.getComment() != null) {
			String comment = feedback.getComment().strip();
			
			if (comment.length() >= 10) {
			    existingFeedback.setComment(comment);  
			}
	    } 
		
		if (feedback.getSuggestion() != null) {
			String suggestion = feedback.getSuggestion().strip();
			
			if (suggestion.length() >= 10) {
			    existingFeedback.setSuggestion(suggestion);  
			}
	    } 
		
		if (feedback.getStatus() != null) {
			existingFeedback.setStatus(feedback.getStatus());
	    } 
	    
	    return toResponse(feedbackRepository.save(existingFeedback));
	}
	
	public void deleteFeedback(int id) {
		Feedback feedback = feedbackRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Feedback with ID \"" + id + "\" not found."));
	    
		feedbackRepository.delete(feedback);
	}
	
	public double getAverageRating() {
		return feedbackRepository.getAverageStars();
	}

	public int getNumberOfNewFeedbacks(FeedbackStatus status) {
		Integer count = feedbackRepository.countByStatus(status);
	    return count != null ? count : 0;
	}
}
