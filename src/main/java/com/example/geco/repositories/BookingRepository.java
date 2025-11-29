package com.example.geco.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.Booking;
import com.example.geco.domains.Booking.BookingStatus;
import com.example.geco.domains.TourPackage;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer>{
	List<Booking> findAllByOrderByVisitDateAscVisitTimeAsc();
	
	Page<Booking> findByAccount_AccountIdAndVisitDateBetween(
			int accountId, 
			LocalDate startDate, 
			LocalDate endDate,
			Pageable pageable);

	List<Booking> findByVisitDate(LocalDate date);
	
	
	Page<Booking> findByVisitDateBetween(
			LocalDate startDate, LocalDate endDate, Pageable pageable
	);
	
	List<Booking> findByVisitDateBetween(
			LocalDate startDate, LocalDate endDate
	);
	
	
	List<Booking> findByVisitDateOrderByVisitTimeAsc(LocalDate visitDate);
	
	
	Page<Booking> findByAccount_AccountIdOrderByVisitDateAscVisitTimeAsc(int id, Pageable pageable);
	List<Booking> findByAccount_AccountIdOrderByVisitDateAscVisitTimeAsc(int id);
	
	
	List<Booking> findByAccount_AccountIdAndBookingStatusOrderByVisitDateAscVisitTimeAsc(
			int id, 
			BookingStatus status);

	List<Booking> findByBookingStatusOrderByVisitDateAscVisitTimeAsc(BookingStatus status);
	
	List<Booking> findByBookingStatusAndVisitDateBetween(
			BookingStatus status, 
			LocalDate startDate, 
			LocalDate endDate);
	
	@Query("""
			SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b 
			WHERE b.visitDate BETWEEN :startDate AND :endDate 
			AND b.bookingStatus = :status
	""")
	Long getTotalRevenueByBookingStatusAndVisitDateBetween(
			@Param("startDate") LocalDate startDate, 
			@Param("endDate") LocalDate endDate, 
			@Param("status") BookingStatus status);

	
	@Query("SELECT MIN(YEAR(b.visitDate)) FROM Booking b")
	Integer getEarliestYear();

	@Query("SELECT MAX(YEAR(b.visitDate)) FROM Booking b")
	Integer getLatestYear();
	
	@Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.bookingStatus = :status")
	Integer findTotalRevenueByStatus(@Param("status") BookingStatus status);
	
	Long countByBookingStatus(BookingStatus status);
	
	Long countByVisitDateBetween(LocalDate start, LocalDate end);

	Long countByBookingStatusAndVisitDateBetween(BookingStatus status, LocalDate start, LocalDate end);

	@Query("""
	    SELECT 
	        COUNT(b) AS totalBookings,
	        SUM(CASE WHEN b.bookingStatus = 'COMPLETED' THEN 1 ELSE 0 END) AS completedBookings,
	        SUM(CASE WHEN b.bookingStatus = 'COMPLETED' THEN b.totalPrice ELSE 0 END) AS totalRevenue
	    FROM Booking b
	    WHERE MONTH(b.visitDate) = :month
	      AND YEAR(b.visitDate) BETWEEN :startYear AND :endYear
	""")
	Object[] getMonthAcrossYearsStats(
	    @Param("month") int month,
	    @Param("startYear") int startYear,
	    @Param("endYear") int endYear
	);

	@Query("""
		SELECT COALESCE(SUM(b.groupSize), 0)
		FROM Booking b 
		WHERE b.bookingStatus = :status 
		AND b.visitDate BETWEEN :start AND :end
			""")
	Long totalGroupSizeByStatusAndVisitDateBetween(
			@Param("status") BookingStatus status,
		    @Param("start") LocalDate startYear,
		    @Param("end") LocalDate endYear);

	Long countByTourPackageAndVisitDateBetween(TourPackage tourPackage, LocalDate startDate, LocalDate endDate);

	List<Booking> findByVisitDateAndBookingIdNotOrderByVisitTimeAsc(LocalDate visitDate, Integer id);

	Page<Booking> findByAccount_AccountIdAndIsActiveOrderByVisitDateAscVisitTimeAsc(
			Integer accountId, boolean isActive, Pageable pageable);

	Page<Booking> findAllByIsActive(boolean isActive, Pageable pageable);

	Page<Booking> findByIsActiveAndVisitDateBetween(
			boolean isActive, LocalDate startDate, LocalDate endDate, Pageable pageable);

	Page<Booking> findByAccount_AccountIdAndIsActiveAndVisitDateBetween(
			Integer accountId, boolean isActive, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
