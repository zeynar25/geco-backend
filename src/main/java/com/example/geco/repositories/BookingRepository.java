package com.example.geco.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.Booking;
import com.example.geco.domains.Booking.BookingStatus;
import com.example.geco.domains.Booking.PaymentMethod;
import com.example.geco.domains.Booking.PaymentStatus;
import com.example.geco.domains.TourPackage;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer>{
	List<Booking> findAllByOrderByVisitDateDescVisitTimeAsc();

	Page<Booking> findAllByOrderByVisitDateDescVisitTimeAsc(Pageable pageable
	);
	
	Page<Booking> findByAccount_AccountIdAndVisitDateBetweenOrderByVisitDateDescVisitTime(
			int accountId, 
			LocalDate startDate, 
			LocalDate endDate,
			Pageable pageable);

	List<Booking> findByVisitDate(LocalDate date);
	
	Page<Booking> findByVisitDateBetweenOrderByVisitDateDesc(
			LocalDate startDate, LocalDate endDate, Pageable pageable
	);
	
	List<Booking> findByVisitDateBetween(
			LocalDate startDate, LocalDate endDate
	);
	
	
	List<Booking> findByVisitDateOrderByVisitTimeAsc(LocalDate visitDate);
	
	
	Page<Booking> findByAccount_AccountIdOrderByVisitDateDescVisitTimeAsc(int id, Pageable pageable);
	List<Booking> findByAccount_AccountIdOrderByVisitDateDescVisitTimeAsc(int id);
	
	
	List<Booking> findByAccount_AccountIdAndBookingStatusOrderByVisitDateDescVisitTimeAsc(
			int id, 
			BookingStatus status);

	List<Booking> findByBookingStatusOrderByVisitDateDescVisitTimeAsc(BookingStatus status);
	
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

	@Query("""
	       SELECT b
	       FROM Booking b
	       WHERE (:accountId IS NULL OR b.account.accountId = :accountId)
	         AND (
	              (:startDate IS NULL AND :endDate IS NULL)
	              OR b.visitDate BETWEEN COALESCE(:startDate, b.visitDate)
	                                 AND COALESCE(:endDate, b.visitDate)
	         )
	         AND (:bookingStatus IS NULL OR b.bookingStatus = :bookingStatus)
	         AND (:paymentStatus IS NULL OR b.paymentStatus = :paymentStatus)
	         AND (:paymentMethod IS NULL OR b.paymentMethod = :paymentMethod)
	         AND (:email IS NULL OR LOWER(b.account.detail.email) LIKE LOWER(CONCAT('%', :email, '%')))
	       ORDER BY b.visitDate DESC, b.visitTime ASC
	       """)
	Page<Booking> findByFilters(
	        @Param("accountId") Integer accountId,
	        @Param("startDate") LocalDate startDate,
	        @Param("endDate") LocalDate endDate,
	        @Param("bookingStatus") BookingStatus bookingStatus,
	        @Param("paymentStatus") PaymentStatus paymentStatus,
	        @Param("paymentMethod") PaymentMethod paymentMethod,
	        @Param("email") String email,
	        Pageable pageable
	);
	
	@Query("""
	    SELECT b
	    FROM Booking b
	    WHERE (:accountId IS NULL OR b.account.accountId = :accountId)
	      AND (
	           (:startDateTime IS NULL AND :endDateTime IS NULL)
	           OR b.createdAt BETWEEN COALESCE(:startDateTime, b.createdAt) AND COALESCE(:endDateTime, b.createdAt)
	      )
	      AND (:bookingStatus IS NULL OR b.bookingStatus = :bookingStatus)
	      AND (:paymentStatus IS NULL OR b.paymentStatus = :paymentStatus)
	      AND (:paymentMethod IS NULL OR b.paymentMethod = :paymentMethod)
	      AND (:email IS NULL OR LOWER(b.account.detail.email) LIKE LOWER(CONCAT('%', :email, '%')))
	    ORDER BY b.visitDate DESC, b.visitTime ASC
	""")
	Page<Booking> findByFiltersByCreatedAt(
	        @Param("accountId") Integer accountId,
	        @Param("startDateTime") LocalDateTime startDateTime,
	        @Param("endDateTime") LocalDateTime endDateTime,
	        @Param("bookingStatus") BookingStatus bookingStatus,
	        @Param("paymentStatus") PaymentStatus paymentStatus,
	        @Param("paymentMethod") PaymentMethod paymentMethod,
	        @Param("email") String email,
	        Pageable pageable
	);

	@Query("""
	    SELECT b
	    FROM Booking b
	    WHERE (:accountId IS NULL OR b.account.accountId = :accountId)
	      AND (
	           (:startDateTime IS NULL AND :endDateTime IS NULL)
	           OR b.updatedAt BETWEEN COALESCE(:startDateTime, b.updatedAt) AND COALESCE(:endDateTime, b.updatedAt)
	      )
	      AND (:bookingStatus IS NULL OR b.bookingStatus = :bookingStatus)
	      AND (:paymentStatus IS NULL OR b.paymentStatus = :paymentStatus)
	      AND (:paymentMethod IS NULL OR b.paymentMethod = :paymentMethod)
	      AND (:email IS NULL OR LOWER(b.account.detail.email) LIKE LOWER(CONCAT('%', :email, '%')))
	    ORDER BY b.visitDate DESC, b.visitTime ASC
	""")
	Page<Booking> findByFiltersByUpdatedAt(
	        @Param("accountId") Integer accountId,
	        @Param("startDateTime") LocalDateTime startDateTime,
	        @Param("endDateTime") LocalDateTime endDateTime,
	        @Param("bookingStatus") BookingStatus bookingStatus,
	        @Param("paymentStatus") PaymentStatus paymentStatus,
	        @Param("paymentMethod") PaymentMethod paymentMethod,
	        @Param("email") String email,
	        Pageable pageable
	);
	
	List<Booking> findByVisitDateAndBookingIdNotOrderByVisitTimeAsc(LocalDate visitDate, Integer id);

	Page<Booking> findByAccount_AccountIdAndIsActiveOrderByVisitDateDescVisitTimeAsc(
			Integer accountId, boolean isActive, Pageable pageable);

	Page<Booking> findAllByIsActive(boolean isActive, Pageable pageable);

	Page<Booking> findByIsActiveAndVisitDateBetween(
			boolean isActive, LocalDate startDate, LocalDate endDate, Pageable pageable);

	Page<Booking> findByAccount_AccountIdAndIsActiveAndVisitDateBetween(
			Integer accountId, boolean isActive, LocalDate startDate, LocalDate endDate, Pageable pageable);



}
