package com.example.geco.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired BookingRepository bookingRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

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
    public List<AuditLog> getLogs(
            LocalDateTime start,
            LocalDateTime end,
            String entityName,   
            AuditLog.LogAction action
    ) {
        Integer earliestYear = bookingRepository.getEarliestYear();

        LocalDateTime startTime = start != null
                ? start
                : (earliestYear != null
                    ? LocalDateTime.of(earliestYear, 1, 1, 0, 0)
                    : LocalDateTime.of(2000, 1, 1, 0, 0));

        LocalDateTime endTime = end != null ? end : LocalDateTime.now();

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start timestamp must be before end timestamp.");
        }

        // If both filters are provided
        if (entityName != null && !entityName.isBlank() && action != null) {
            return auditLogRepository
                    .findAllByEntityNameAndActionAndTimestampBetweenOrderByTimestampDesc(
                            entityName, action, startTime, endTime);
        }
        
        // Only entityName filter
        else if (entityName != null && !entityName.isBlank()) {
            return auditLogRepository
                    .findAllByEntityNameAndTimestampBetweenOrderByTimestampDesc(
                            entityName, startTime, endTime);
        }
        
        // Only action filter
        else if (action != null) {
            return auditLogRepository
                    .findAllByActionAndTimestampBetweenOrderByTimestampDesc(
                            action, startTime, endTime);
        }
        
        // No filters
        else {
            return auditLogRepository
                    .findAllByTimestampBetweenOrderByTimestampDesc(startTime, endTime);
        }
    }

}
