package com.example.geco.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.Feedback;
import com.example.geco.domains.Feedback.FeedbackStatus;

@Repository
public interface FeedbackRepository  extends JpaRepository<Feedback, Integer>{
	@Query("SELECT COALESCE(AVG(f.stars), 0) FROM Feedback f")
	double getAverageStars();
	
	boolean existsByBooking_BookingIdAndAccount_AccountId(Integer bookingId, Integer accountId);

	Integer countByFeedbackStatus(FeedbackStatus feedbackStatus);

	Page<Feedback> findByBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
			LocalDate startDate, LocalDate endDate, Pageable pageable);

	List<Feedback> findByCategory_FeedbackCategoryIdOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(Integer categoryId);

	Page<Feedback> findByCategory_FeedbackCategoryIdAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
			Integer categoryId,
			LocalDate startDate, 
			LocalDate endDate, 
			Pageable pageable);

	Page<Feedback> findByIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(boolean isActive, LocalDate startDate,
			LocalDate endDate, Pageable pageable);

	Page<Feedback> findByCategory_FeedbackCategoryIdAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
			Integer categoryId, boolean isActive, LocalDate startDate, LocalDate endDate, Pageable pageable);

	Page<Feedback> findByAccount_AccountIdAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
			int accountId, LocalDate startDate, LocalDate endDate, Pageable pageable);

	Page<Feedback> findByCategory_FeedbackCategoryIdAndAccount_AccountIdAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
			Integer categoryId, int accountId, LocalDate startDate, LocalDate endDate, Pageable pageable);

	Page<Feedback> findByFeedbackStatusAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	        FeedbackStatus feedbackStatus,
	        LocalDate startDate,
	        LocalDate endDate,
	        Pageable pageable);

	Page<Feedback> findByFeedbackStatusAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	        FeedbackStatus feedbackStatus,
	        boolean isActive,
	        LocalDate startDate,
	        LocalDate endDate,
	        Pageable pageable);

	Page<Feedback> findByCategory_FeedbackCategoryIdAndFeedbackStatusAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	        Integer categoryId,
	        FeedbackStatus feedbackStatus,
	        LocalDate startDate,
	        LocalDate endDate,
	        Pageable pageable);

	Page<Feedback> findByCategory_FeedbackCategoryIdAndFeedbackStatusAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	        Integer categoryId,
	        FeedbackStatus feedbackStatus,
	        boolean isActive,
	        LocalDate startDate,
	        LocalDate endDate,
	        Pageable pageable);
}
