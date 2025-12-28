package com.example.geco.repositories;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.AuditLog;
import com.example.geco.domains.AuditLog.LogAction;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

	Page<AuditLog> findAllByTimestampBetween(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    Page<AuditLog> findAllByEntityNameAndActionAndTimestampBetween(
            String entityName,
            LogAction action,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable
    );

    Page<AuditLog> findAllByEntityNameAndTimestampBetween(
            String entityName,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable
    );

    Page<AuditLog> findAllByActionAndTimestampBetween(
            LogAction action,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable
    );
}