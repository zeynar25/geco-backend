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
			int accountId, 
			LocalDate startDate, 
			LocalDate endDate);

	List<Booking> findByVisitDate(LocalDate date);
	
	List<Booking> findByVisitDateBetween(
			LocalDate startDate, LocalDate endDate
	);
	
	List<Booking> findByVisitDateOrderByVisitTimeAsc(LocalDate visitDate);
	
	List<Booking> findByAccount_AccountIdOrderByVisitDateAscVisitTimeAsc(int id);
	
	List<Booking> findByAccount_AccountIdAndStatusOrderByVisitDateAscVisitTimeAsc(
			int id, 
			BookingStatus status);

	List<Booking> findByStatusOrderByVisitDateAscVisitTimeAsc(BookingStatus status);
	
	List<Booking> findByStatusAndVisitDateBetween(
			BookingStatus status, 
			LocalDate startDate, 
			LocalDate endDate);
	
	@Query("""
			SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b 
			WHERE b.visitDate BETWEEN :startDate AND :endDate 
			AND b.status = :status
	""")
	Long getRevenue(
			@Param("startDate") LocalDate startDate, 
			@Param("endDate") LocalDate endDate, 
			@Param("status") BookingStatus status);

	
	@Query("SELECT MIN(YEAR(b.visitDate)) FROM Booking b")
	Integer getEarliestYear();

	@Query("SELECT MAX(YEAR(b.visitDate)) FROM Booking b")
	Integer getLatestYear();
	
	@Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.status = :status")
	Integer findTotalRevenueByStatus(@Param("status") Booking.BookingStatus status);
	
	Long countByStatus(BookingStatus status);
	
	int countByVisitDateBetween(LocalDate start, LocalDate end);

	int countByStatusAndVisitDateBetween(BookingStatus status, LocalDate start, LocalDate end);

	@Query("""
	    SELECT 
	        COUNT(b) AS totalBookings,
	        SUM(CASE WHEN b.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completedBookings,
	        SUM(CASE WHEN b.status = 'COMPLETED' THEN b.totalPrice ELSE 0 END) AS totalRevenue
	    FROM Booking b
	    WHERE MONTH(b.visitDate) = :month
	      AND YEAR(b.visitDate) BETWEEN :startYear AND :endYear
	""")
	Object[] getMonthAcrossYearsStats(
	    @Param("month") int month,
	    @Param("startYear") int startYear,
	    @Param("endYear") int endYear
	);
}
