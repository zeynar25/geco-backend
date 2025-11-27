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
	@Query("SELECT COALESCE(AVG(f.stars), 0) FROM Feedback f")
	double getAverageStars();
	
	boolean existsByBooking_BookingIdAndAccount_AccountId(Integer bookingId, Integer accountId);

	Integer countByFeedbackStatus(FeedbackStatus feedbackStatus);

	List<Feedback> findByBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(LocalDate startDate, LocalDate endDate);

	List<Feedback> findByCategory_FeedbackCategoryIdOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(Integer categoryId);

	List<Feedback> findByCategory_FeedbackCategoryIdAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
			Integer categoryId,
			LocalDate startDate, 
			LocalDate endDate);

	List<Feedback> findByIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(boolean isActive, LocalDate startDate,
			LocalDate endDate);

	List<Feedback> findByCategory_FeedbackCategoryIdAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
			Integer categoryId, boolean isActive, LocalDate startDate, LocalDate endDate);

	List<Feedback> findByAccount_AccountIdAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
			int accountId, LocalDate startDate, LocalDate endDate);

	List<Feedback> findByCategory_FeedbackCategoryIdAndAccount_AccountIdAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
			Integer categoryId, int accountId, LocalDate startDate, LocalDate endDate);
}
