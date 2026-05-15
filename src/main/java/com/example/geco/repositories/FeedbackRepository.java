package com.example.geco.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
	
	Page<Feedback> findByAccount_Detail_EmailContainingIgnoreCaseAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	    String email, 
	    LocalDate startDate, 
	    LocalDate endDate, 
	    Pageable pageable
	);

	Page<Feedback> findByAccount_Detail_EmailContainingIgnoreCaseAndFeedbackStatusAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	    String email,
	    FeedbackStatus feedbackStatus,
	    LocalDate startDate,
	    LocalDate endDate,
	    Pageable pageable
	);

	Page<Feedback> findByAccount_Detail_EmailContainingIgnoreCaseAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	    String email,
	    boolean isActive,
	    LocalDate startDate,
	    LocalDate endDate,
	    Pageable pageable
	);

	Page<Feedback> findByAccount_Detail_EmailContainingIgnoreCaseAndFeedbackStatusAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	    String email,
	    FeedbackStatus feedbackStatus,
	    boolean isActive,
	    LocalDate startDate,
	    LocalDate endDate,
	    Pageable pageable
	);

	Page<Feedback> findByCategory_FeedbackCategoryIdAndAccount_Detail_EmailContainingIgnoreCaseAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	    Integer categoryId,
	    String email,
	    LocalDate startDate,
	    LocalDate endDate,
	    Pageable pageable
	);

	Page<Feedback> findByCategory_FeedbackCategoryIdAndAccount_Detail_EmailContainingIgnoreCaseAndFeedbackStatusAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	    Integer categoryId,
	    String email,
	    FeedbackStatus feedbackStatus,
	    LocalDate startDate,
	    LocalDate endDate,
	    Pageable pageable
	);

	Page<Feedback> findByCategory_FeedbackCategoryIdAndAccount_Detail_EmailContainingIgnoreCaseAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	    Integer categoryId,
	    String email,
	    boolean isActive,
	    LocalDate startDate,
	    LocalDate endDate,
	    Pageable pageable
	);

	Page<Feedback> findByCategory_FeedbackCategoryIdAndAccount_Detail_EmailContainingIgnoreCaseAndFeedbackStatusAndIsActiveAndBooking_VisitDateBetweenOrderByFeedbackStatusAscBooking_VisitDateDescBooking_VisitTimeDesc(
	    Integer categoryId,
	    String email,
	    FeedbackStatus feedbackStatus,
	    boolean isActive,
	    LocalDate startDate,
	    LocalDate endDate,
	    Pageable pageable
	);
	
	@Query("""
		SELECT f FROM Feedback f
		WHERE f.booking.visitDate BETWEEN :startDate AND :endDate
		  AND (:categoryId IS NULL OR f.category.feedbackCategoryId = :categoryId)
		  AND (:feedbackStatus IS NULL OR f.feedbackStatus = :feedbackStatus)
		  AND (:isActive IS NULL OR f.isActive = :isActive)
		  AND (:email IS NULL OR LOWER(f.account.detail.email) LIKE LOWER(CONCAT('%', :email, '%')))
		  AND (:minStars IS NULL OR (f.stars >= :minStars AND f.stars < :maxStars))
		ORDER BY f.feedbackStatus ASC, f.booking.visitDate DESC, f.booking.visitTime DESC
		""")
	Page<Feedback> searchFeedbacks(
	    @Param("categoryId") Integer categoryId,
	    @Param("startDate") LocalDate startDate,
	    @Param("endDate") LocalDate endDate,
	    @Param("feedbackStatus") FeedbackStatus feedbackStatus,
	    @Param("isActive") Boolean isActive,
	    @Param("email") String email,
	    @Param("minStars") Double minStars,
	    @Param("maxStars") Double maxStars,
	    Pageable pageable
	);
	
	@Query("""
	SELECT f FROM Feedback f
	WHERE f.account.accountId = :accountId
	  AND f.booking.visitDate BETWEEN :startDate AND :endDate
	  AND (:categoryId IS NULL OR f.category.feedbackCategoryId = :categoryId)
	  AND (:minStars IS NULL OR (f.stars >= :minStars AND f.stars < :maxStars))
	ORDER BY f.feedbackStatus ASC, f.booking.visitDate DESC, f.booking.visitTime DESC
	""")
	Page<Feedback> searchMyFeedbacks(
	    @Param("accountId") int accountId,
	    @Param("categoryId") Integer categoryId,
	    @Param("startDate") LocalDate startDate,
	    @Param("endDate") LocalDate endDate,
	    @Param("minStars") Double minStars,
	    @Param("maxStars") Double maxStars,
	    Pageable pageable
	);
}
