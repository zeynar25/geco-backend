package com.example.geco.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.Booking;
import com.example.geco.domains.Booking.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer>{
	List<Booking> findAllByOrderByVisitDateAscVisitTimeAsc();
	
	List<Booking> findByAccount_AccountIdAndVisitDateBetween(
			int accountId, LocalDate startDate, LocalDate endDate
	);

	List<Booking> findByVisitDate(LocalDate date);
	
	List<Booking> findByVisitDateBetween(
			LocalDate startDate, LocalDate endDate
	);
	
	List<Booking> findByAccount_AccountIdOrderByVisitDateAscVisitTimeAsc(int id);

	List<Booking> findByStatusOrderByVisitDateAscVisitTimeAsc(BookingStatus status);
	
	List<Booking> findByAccount_AccountIdAndStatusOrderByVisitDateAscVisitTimeAsc(int id, BookingStatus status);
	
	List<Booking> findByVisitDateOrderByVisitTimeAsc(LocalDate visitDate);
	
	@Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.visitDate BETWEEN :startDate AND :endDate AND b.status = :status")
	Integer getRevenueByMonth(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") BookingStatus status);

	Long countByStatus(BookingStatus status);
}
