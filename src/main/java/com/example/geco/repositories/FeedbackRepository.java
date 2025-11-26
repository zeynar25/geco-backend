package com.example.geco.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.Feedback;
import com.example.geco.domains.Feedback.FeedbackStatus;

@Repository
public interface FeedbackRepository  extends JpaRepository<Feedback, Integer>{
	@Query("SELECT AVG(f.stars) FROM Feedback f")
	double getAverageStars();
	
	boolean existsByBooking_BookingIdAndAccount_AccountId(Integer bookingId, Integer accountId);

	Integer countByFeedbackStatus(FeedbackStatus feedbackStatus);

	List<Feedback> findByBooking_VisitDateBetweenOrderByFeedbackStatus(LocalDate startDate, LocalDate endDate);

	List<Feedback> findByCategory_FeedbackCategoryIdOrderByFeedbackStatus(Integer categoryId);

	List<Feedback> findByCategory_FeedbackCategoryIdAndBooking_VisitDateBetweenOrderByFeedbackStatus(
			Integer categoryId,
			LocalDate startDate, 
			LocalDate endDate);

	List<Feedback> findByIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatus(boolean isActive, LocalDate startDate,
			LocalDate endDate);

	List<Feedback> findByCategory_FeedbackCategoryIdAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatus(
			Integer categoryId, boolean isActive, LocalDate startDate, LocalDate endDate);
}
