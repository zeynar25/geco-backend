package com.example.geco.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
				.staffReply(feedback.getStaffReply())
				.feedbackStatus(feedback.getFeedbackStatus())
				.isActive(feedback.isActive())
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
				.staffReply(feedback.getStaffReply())
				.feedbackStatus(feedback.getFeedbackStatus())
				.isActive(feedback.isActive())
				.build();
	}
	
	public FeedbackResponse addFeedback(FeedbackRequest request) {
		Integer accountId = getLoggedAccountId();
		Integer bookingId = request.getBookingId();
		Integer feedbackCategoryId = request.getCategoryId();
		Double stars = request.getStars();
		String comment = request.getComment() != null ? request.getComment().trim() : null;
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
		
		Feedback feedback = Feedback.builder()
				.account(account)
				.booking(booking)
				.category(existingCategory)
				.stars(stars)
				.comment(comment)
				.suggestion(suggestion)
				.feedbackStatus(FeedbackStatus.NEW)
				.isActive(false)
				.build();
		
		Feedback savedFeedback = feedbackRepository.save(feedback);

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
	
	private Page<FeedbackResponse> mapToResponse(Page<Feedback> feedbacks) {
	    return feedbacks.map(this::toResponse);
	}

	@Transactional(readOnly = true)
	public Page<FeedbackResponse> getMyFeedbacks(
			Integer categoryId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
		startDate = defaultStartDate(startDate);
	    endDate = defaultEndDate(endDate);
	    validateDateRange(startDate, endDate);
	    
	    int accountId = getLoggedAccountId();
	
	    Page<Feedback> feedbacks;
	
	    if (categoryId == null) {
            feedbacks = feedbackRepository
            		.findByAccount_AccountIdAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
            				accountId, startDate, endDate, pageable);
	        
	    } else {
            feedbacks = feedbackRepository
            		.findByCategory_FeedbackCategoryIdAndAccount_AccountIdAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
            				categoryId, accountId, startDate, endDate, pageable);
	    }
	
	    return mapToResponse(feedbacks);
	}
	
	@Transactional(readOnly = true)
	public Page<FeedbackResponse> getFeedbacks(
	        Integer categoryId,
	        LocalDate startDate,
	        LocalDate endDate,
	        FeedbackStatus feedbackStatus,
	        Boolean isActive,
	        String email,
	        Pageable pageable
	) {
	    startDate = defaultStartDate(startDate);
	    endDate = defaultEndDate(endDate);
	    validateDateRange(startDate, endDate);

	    String q = email == null ? "" : email.trim();
	    boolean hasEmail = !q.isEmpty();

	    Page<Feedback> feedbacks;

	    if (!hasEmail) {
	        if (categoryId == null) {
	            if (isActive == null) {
	                if (feedbackStatus == null) {
	                    feedbacks =
	                        feedbackRepository.findByBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                            startDate, endDate, pageable);
	                } else {
	                    feedbacks =
	                        feedbackRepository.findByFeedbackStatusAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                            feedbackStatus, startDate, endDate, pageable);
	                }
	            } else {
	                if (feedbackStatus == null) {
	                    feedbacks =
	                        feedbackRepository.findByIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                            isActive, startDate, endDate, pageable);
	                } else {
	                    feedbacks =
	                        feedbackRepository.findByFeedbackStatusAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                            feedbackStatus, isActive, startDate, endDate, pageable);
	                }
	            }
	        } else {
	            if (isActive == null) {
	                if (feedbackStatus == null) {
	                    feedbacks =
	                        feedbackRepository.findByCategory_FeedbackCategoryIdAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                            categoryId, startDate, endDate, pageable);
	                } else {
	                    feedbacks =
	                        feedbackRepository.findByCategory_FeedbackCategoryIdAndFeedbackStatusAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                            categoryId, feedbackStatus, startDate, endDate, pageable);
	                }
	            } else {
	                if (feedbackStatus == null) {
	                    feedbacks =
	                        feedbackRepository.findByCategory_FeedbackCategoryIdAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                            categoryId, isActive, startDate, endDate, pageable);
	                } else {
	                    feedbacks =
	                        feedbackRepository.findByCategory_FeedbackCategoryIdAndFeedbackStatusAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                            categoryId, feedbackStatus, isActive, startDate, endDate, pageable);
	                }
	            }
	        }
	    } else {
	        if (categoryId == null) {
	            if (isActive == null) {
	                if (feedbackStatus == null) {
	                    feedbacks =
	                        feedbackRepository
	                            .findByAccount_Detail_EmailContainingIgnoreCaseAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                                q, startDate, endDate, pageable);
	                } else {
	                    feedbacks =
	                        feedbackRepository
	                            .findByAccount_Detail_EmailContainingIgnoreCaseAndFeedbackStatusAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                                q, feedbackStatus, startDate, endDate, pageable);
	                }
	            } else {
	                if (feedbackStatus == null) {
	                    feedbacks =
	                        feedbackRepository
	                            .findByAccount_Detail_EmailContainingIgnoreCaseAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                                q, isActive, startDate, endDate, pageable);
	                } else {
	                    feedbacks =
	                        feedbackRepository
	                            .findByAccount_Detail_EmailContainingIgnoreCaseAndFeedbackStatusAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                                q, feedbackStatus, isActive, startDate, endDate, pageable);
	                }
	            }
	        } else {
	            if (isActive == null) {
	                if (feedbackStatus == null) {
	                    feedbacks =
	                        feedbackRepository
	                            .findByCategory_FeedbackCategoryIdAndAccount_Detail_EmailContainingIgnoreCaseAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                                categoryId, q, startDate, endDate, pageable);
	                } else {
	                    feedbacks =
	                        feedbackRepository
	                            .findByCategory_FeedbackCategoryIdAndAccount_Detail_EmailContainingIgnoreCaseAndFeedbackStatusAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                                categoryId, q, feedbackStatus, startDate, endDate, pageable);
	                }
	            } else {
	                if (feedbackStatus == null) {
	                    feedbacks =
	                        feedbackRepository
	                            .findByCategory_FeedbackCategoryIdAndAccount_Detail_EmailContainingIgnoreCaseAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                                categoryId, q, isActive, startDate, endDate, pageable);
	                } else {
	                    feedbacks =
	                        feedbackRepository
	                            .findByCategory_FeedbackCategoryIdAndAccount_Detail_EmailContainingIgnoreCaseAndFeedbackStatusAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	                                categoryId, q, feedbackStatus, isActive, startDate, endDate, pageable);
	                }
	            }
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
	    String staffReply = request.getStaffReply() != null ? request.getStaffReply().trim() : null;
	    FeedbackStatus feedbackStatus = request.getFeedbackStatus();
	    
		if (
				staffReply == null
				&& feedbackStatus == null) {
			throw new IllegalArgumentException("No fields provided to update feedback.");
		}
		
		Feedback existingFeedback = feedbackRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Feedback with ID '" + id + "' not found."));
		
		Feedback prevFeedback = createFeedbackCopy(existingFeedback);
		
		if (staffReply != null) {
		    existingFeedback.setStaffReply(staffReply); 
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
