package com.example.geco.repositories;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer>{
	Page<Notification> findByAccount_AccountIdAndIsActive(int accountId, boolean isActive, Pageable pageable);

	Page<Notification> findByAccount_AccountIdAndReadAndIsActive(int accountId, boolean isRead, boolean isActive, Pageable pageable);
	
	@Modifying
	@Query("UPDATE Notification n SET n.read = true WHERE n.account.accountId = :accountId")
	void markAllAsReadByAccountId(@Param("accountId") int accountId);
	
	@Query("""
	    SELECT n FROM Notification n
	    WHERE n.account.accountId = :accountId
	      AND n.isActive = :isActive
	      AND (:isRead IS NULL OR n.read = :isRead)
	      AND (:startDate IS NULL OR n.createdAt >= :startDate)
	      AND (:endDate IS NULL OR n.createdAt <= :endDate)
	    ORDER BY n.createdAt DESC
	""")
	Page<Notification> searchMyNotifications(
	    @Param("accountId") int accountId,
	    @Param("isRead") Boolean isRead,
	    @Param("startDate") LocalDateTime startDate,
	    @Param("endDate") LocalDateTime endDate,
	    @Param("isActive") boolean isActive,
	    Pageable pageable
	);
}
