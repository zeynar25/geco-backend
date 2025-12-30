package com.example.geco.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.geco.domains.Account.Role;
import com.example.geco.domains.AuditLog;
import com.example.geco.domains.AuditLog.LogAction;
import com.example.geco.repositories.AuditLogRepository;
import com.example.geco.repositories.BookingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Autowired 
    BookingRepository bookingRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    private String json(Object value) {
        try {
            return value == null ? null : objectMapper.writeValueAsString(value);
            
        } catch (Exception e) {
            return "JSON_ERROR: " + e.getMessage();
        }
    }

    public void logAction(
            String entityName,
            Long entityId,
            LogAction action,
            Object oldValue,
            Object newValue,
            Integer id,
            String email,
            Role role) {
    	auditLogRepository.save(
    		    AuditLog.builder()
    		        .entityName(entityName)
    		        .entityId(entityId)
    		        .action(action)
    		        .oldValue(json(oldValue))
    		        .newValue(json(newValue))
    		        .performedByAccountId(id)
    		        .performedByEmail(email)
    		        .performedByRole(role)
    		        .build()
    		);
    }
    
    @Transactional(readOnly = true)
    public Page<AuditLog> getLogs(
            LocalDateTime start,
            LocalDateTime end,
            String entityName,
            AuditLog.LogAction action,
            int page,
            int size
    ) {
    	Integer earliestYear = bookingRepository.getEarliestYear();

        int year = (earliestYear != null) ? earliestYear : LocalDateTime.now().getYear();

        LocalDateTime startTime = start != null
            ? start
            : LocalDateTime.of(year, 1, 1, 0, 0);

        LocalDateTime endTime = end != null
            ? end
            : LocalDateTime.of(year, 12, 31, 23, 59, 59);

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start timestamp must be before end timestamp.");
        }

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start timestamp must be before end timestamp.");
        }

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start timestamp must be before end timestamp.");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "timestamp")
        );

        // Both filters
        if (entityName != null && !entityName.isBlank() && action != null) {
            return auditLogRepository
                    .findAllByEntityNameAndActionAndTimestampBetween(
                            entityName, action, startTime, endTime, pageable);
        }
        // Only entityName
        else if (entityName != null && !entityName.isBlank()) {
            return auditLogRepository
                    .findAllByEntityNameAndTimestampBetween(
                            entityName, startTime, endTime, pageable);
        }
        // Only action
        else if (action != null) {
            return auditLogRepository
                    .findAllByActionAndTimestampBetween(
                            action, startTime, endTime, pageable);
        }
        // No filters
        else {
            return auditLogRepository
                    .findAllByTimestampBetween(startTime, endTime, pageable);
        }
    }

}
