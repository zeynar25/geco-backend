package com.example.geco.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.AuditLog;
import com.example.geco.domains.AuditLog.LogAction;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

	List<AuditLog> findAllByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);

	List<AuditLog> findAllByEntityNameAndActionAndTimestampBetweenOrderByTimestampDesc(String entityName,
			LogAction action, LocalDateTime startTime, LocalDateTime endTime);

	List<AuditLog> findAllByEntityNameAndTimestampBetweenOrderByTimestampDesc(String entityName,
			LocalDateTime startTime, LocalDateTime endTime);

	List<AuditLog> findAllByActionAndTimestampBetweenOrderByTimestampDesc(LogAction action, LocalDateTime startTime,
			LocalDateTime endTime);
}