package com.example.geco.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.geco.domains.Account.Role;
import com.example.geco.domains.AuditLog;
import com.example.geco.repositories.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;
    
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
            String action,
            Object oldValue,
            Object newValue,
            String email,
            Role role) {
    	auditLogRepository.save(
    		    AuditLog.builder()
    		        .entityName(entityName)
    		        .entityId(entityId)
    		        .action(action)
    		        .oldValue(json(oldValue))
    		        .newValue(json(newValue))
    		        .email(email)
    		        .performedByRole(role)
    		        .build()
    		);
    }
}
