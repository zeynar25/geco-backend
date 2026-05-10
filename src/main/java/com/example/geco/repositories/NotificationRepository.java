package com.example.geco.repositories;

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
}
