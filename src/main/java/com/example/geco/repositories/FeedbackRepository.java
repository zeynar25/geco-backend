package com.example.geco.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.Feedback;

@Repository
public interface FeedbackRepository  extends JpaRepository<Feedback, Integer>{
	@Query("SELECT AVG(f.stars) FROM Feedback f")
	double getAverageStars();
	
	boolean existsByBooking_BookingIdAndAccount_AccountId(Integer bookingId, Integer accountId);
	
	List<Feedback> findByCategory_FeedbackCategoryIdAndBooking_VisitDateBetween(
			int categoryId, LocalDate startDate, LocalDate endDate
	);
}
