package com.example.geco.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.geco.domains.Account;
import com.example.geco.domains.AuditLog.LogAction;
import com.example.geco.domains.Booking;
import com.example.geco.domains.Feedback;
import com.example.geco.domains.Feedback.FeedbackStatus;
import com.example.geco.domains.FeedbackCategory;
import com.example.geco.dto.FeedbackRequest;
import com.example.geco.dto.FeedbackResponse;
import com.example.geco.dto.FeedbackUpdateRequest;
import com.example.geco.dto.UserFeedbackUpdateRequest;
import com.example.geco.repositories.AccountRepository;
import com.example.geco.repositories.BookingRepository;
import com.example.geco.repositories.FeedbackCategoryRepository;
import com.example.geco.repositories.FeedbackRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class FeedbackService extends BaseService{
	@Autowired
	private FeedbackCategoryRepository feedbackCategoryRepository;
	
	@Autowired
	private FeedbackRepository feedbackRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	public FeedbackResponse toResponse(Feedback feedback) {
		return FeedbackResponse.builder()
				.feedbackId(feedback.getFeedbackId())
				.account(feedback.getAccount())
				.booking(feedback.getBooking())
				.category(feedback.getCategory().getLabel())
				.stars(feedback.getStars())
				.comment(feedback.getComment())
				.suggestion(feedback.getSuggestion())
				.feedbackStatus(feedback.getFeedbackStatus())
				.build();
	}
	
	public Feedback createFeedbackCopy(Feedback feedback) {
		return Feedback.builder()
				.feedbackId(feedback.getFeedbackId())
				.account(feedback.getAccount())
				.booking(feedback.getBooking())
				.category(feedback.getCategory())
				.stars(feedback.getStars())
				.comment(feedback.getComment())
				.suggestion(feedback.getSuggestion())
				.feedbackStatus(feedback.getFeedbackStatus())
				.isActive(feedback.isActive())
				.build();
	}
	
	public FeedbackResponse addFeedback(FeedbackRequest request) {
		Integer accountId = getLoggedAccountId();
		Integer bookingId = request.getBookingId();
		Integer feedbackCategoryId = request.getCategoryId();
		Double stars = request.getStars();
		String comment = request.getComment().trim();
		String suggestion = request.getSuggestion() != null ? request.getSuggestion().trim() : null;
		
	    Account account = accountRepository.findById(accountId)
	    	.orElseThrow(
	    		() -> new EntityNotFoundException("Account with ID '" + accountId + "' not found."));

	    Booking booking = bookingRepository.findById(bookingId)
	    	.orElseThrow(
	    		() -> new EntityNotFoundException("Booking with ID '" + bookingId + "' not found."));
		
		// Check if the account's feedback for this booking already exist.
		if (feedbackRepository.existsByBooking_BookingIdAndAccount_AccountId(
		        bookingId,
		        accountId)) {
		    throw new IllegalArgumentException("Feedback for this booking by this account already exists.");
		}
		
		FeedbackCategory existingCategory = feedbackCategoryRepository.findById(
				feedbackCategoryId)
					.orElseThrow(() -> new EntityNotFoundException(
							"Feedback category with ID '" + feedbackCategoryId + "' not found."));
		
		Feedback savedFeedback = Feedback.builder()
				.account(account)
				.booking(booking)
				.category(existingCategory)
				.stars(stars)
				.comment(comment)
				.suggestion(suggestion)
				.feedbackStatus(FeedbackStatus.NEW)
				.build();

	    logIfStaffOrAdmin("Feedback", (long) savedFeedback.getFeedbackId(), LogAction.CREATE, null, savedFeedback);
	    
	    return toResponse(feedbackRepository.save(savedFeedback));
	}

	@Transactional(readOnly = true)
	public FeedbackResponse getFeedback(int id) {
		Feedback feedback = feedbackRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Feedback with ID '" + id + "' not found."));
	    
	    return toResponse(feedback);
	}
	
	private void validateDateRange(LocalDate startDate, LocalDate endDate) {
	    if (endDate.isBefore(startDate)) {
	        throw new IllegalArgumentException("End date cannot be earlier than start date.");
	    }
	}
	
	private LocalDate defaultStartDate(LocalDate startDate) {
	    return startDate != null ? startDate : LocalDate.of(bookingRepository.getEarliestYear(), 1, 1);
	}
	
	private LocalDate defaultEndDate(LocalDate endDate) {
	    return endDate != null ? endDate : LocalDate.now();
	}
	
	private List<FeedbackResponse> mapToResponse(List<Feedback> feedbacks) {
	    return feedbacks.stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public List<FeedbackResponse> getMyFeedbacks(Integer categoryId, LocalDate startDate, LocalDate endDate) {
		startDate = defaultStartDate(startDate);
	    endDate = defaultEndDate(endDate);
	    validateDateRange(startDate, endDate);
	    
	    int accountId = getLoggedAccountId();
	
	    List<Feedback> feedbacks;
	
	    if (categoryId == null) {
            feedbacks = feedbackRepository
            		.findByAccount_AccountIdAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
            				accountId, startDate, endDate);
	        
	    } else {
            feedbacks = feedbackRepository
            		.findByCategory_FeedbackCategoryIdAndAccount_AccountIdAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
            				categoryId, accountId, startDate, endDate);
	    }
	
	    return mapToResponse(feedbacks);
	}

	@Transactional(readOnly = true)
	public List<FeedbackResponse> getFeedbacks(
	        Integer categoryId,
	        LocalDate startDate,
	        LocalDate endDate,
	        Boolean isActive // null = all, true = active, false = inactive
	) {
	    startDate = defaultStartDate(startDate);
	    endDate = defaultEndDate(endDate);
	    validateDateRange(startDate, endDate);
	
	    List<Feedback> feedbacks;
	
	    if (categoryId == null) {
	        if (isActive == null) {
	            feedbacks = feedbackRepository
	            		.findByBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	            				startDate, endDate);
	        
	        } else {
	            feedbacks = feedbackRepository
	            		.findByIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	            				isActive, startDate, endDate);
	        }
	        
	    } else {
	        if (isActive == null) {
	            feedbacks = feedbackRepository
	            		.findByCategory_FeedbackCategoryIdAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	            				categoryId, startDate, endDate);
	        
	        } else {
	            feedbacks = feedbackRepository.findByCategory_FeedbackCategoryIdAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                    categoryId, isActive, startDate, endDate);
	        }
	    }
	
	    return mapToResponse(feedbacks);
	}

	public FeedbackResponse updateFeedback(int id, UserFeedbackUpdateRequest request) {
		Double stars = request.getStars();
	    String comment = request.getComment() != null ? request.getComment().trim() : null;
	    String suggestion = request.getSuggestion() != null ? request.getSuggestion().trim() : null;
	    
		if (stars == null 
				&& comment == null 
				&& suggestion == null) {
			throw new IllegalArgumentException("No fields provided to update feedback.");
		}
		
		Feedback existingFeedback = feedbackRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Feedback with ID '" + id + "' not found."));
		
		Feedback prevFeedback = createFeedbackCopy(existingFeedback);
		
		if (stars != null) {
			stars = Math.round(stars * 10.0) / 10.0;
			existingFeedback.setStars(stars); 
	    } 
		
		if (comment != null) {
		    existingFeedback.setComment(comment);  
	    } 
		
		if (suggestion != null) {
			if (suggestion.length() >= 10) {
			    existingFeedback.setSuggestion(suggestion);  
			} else {
				throw new IllegalArgumentException("Suggestion must be at least 10 characters");
			}
	    } 
		
		logIfStaffOrAdmin("Feedback", (long) id, LogAction.UPDATE, prevFeedback, existingFeedback);
	    
	    return toResponse(feedbackRepository.save(existingFeedback));
	}
	
	// Can update the feedbackStatus to viewed.
	public FeedbackResponse updateFeedbackByStaff(int id, FeedbackUpdateRequest request) {
		Double stars = request.getStars();
	    String comment = request.getComment() != null ? request.getComment().trim() : null;
	    String suggestion = request.getSuggestion() != null ? request.getSuggestion().trim() : null;
		FeedbackStatus feedbackStatus = request.getFeedbackStatus();
	    
		if (stars == null 
				&& comment == null 
				&& suggestion == null 
				&& feedbackStatus == null) {
			throw new IllegalArgumentException("No fields provided to update feedback.");
		}
		
		Feedback existingFeedback = feedbackRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Feedback with ID '" + id + "' not found."));
		
		Feedback prevFeedback = createFeedbackCopy(existingFeedback);
		
		if (stars != null) {
			stars = Math.round(stars * 10.0) / 10.0;
			existingFeedback.setStars(stars); 
	    } 
		
		if (comment != null) {
		    existingFeedback.setComment(comment);  
	    } 
		
		if (suggestion != null) {
			if (suggestion.length() >= 10) {
			    existingFeedback.setSuggestion(suggestion);  
			} else {
				throw new IllegalArgumentException("Suggestion must be at least 10 characters");
			}
	    } 
		
		if (feedbackStatus != null && !feedbackStatus.equals(existingFeedback.getFeedbackStatus())) {
		    existingFeedback.setFeedbackStatus(feedbackStatus);  
		}
		
		logIfStaffOrAdmin("Feedback", (long) id, LogAction.UPDATE, prevFeedback, existingFeedback);
	    
	    return toResponse(feedbackRepository.save(existingFeedback));
	}
	
	public void softDeleteFeedback(int id) {
		Feedback feedback = feedbackRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Feedback with ID '" + id + "' not found."));
	    
		if (!feedback.isActive()) {
	        throw new IllegalStateException("Account is already disabled.");
	    }
	    
	    Feedback prevFeedback = createFeedbackCopy(feedback);

	    feedback.setActive(false);
	    feedbackRepository.save(feedback);
	    
	    logIfStaffOrAdmin("Feedback", (long) id, LogAction.DISABLE, prevFeedback, feedback);
	}
	
	public void restoreFeedback(int id) {
		Feedback feedback = feedbackRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Feedback with ID '" + id + "' not found."));
	    
		if (feedback.isActive()) {
	        throw new IllegalStateException("Account is already active.");
	    }
	    
	    Feedback prevFeedback = createFeedbackCopy(feedback);

	    feedback.setActive(true);
	    feedbackRepository.save(feedback);
	    
	    logIfStaffOrAdmin("Feedback", (long) id, LogAction.RESTORE, prevFeedback, feedback);
	}

	@Transactional(readOnly = true)
	public double getAverageRating() {
		return feedbackRepository.getAverageStars();
	}

	@Transactional(readOnly = true)
	public int getNumberOfNewFeedbacks(FeedbackStatus FeedbackStatus) {
		Integer count = feedbackRepository.countByFeedbackStatus(FeedbackStatus);
	    return count != null ? count : 0;
	}
}
