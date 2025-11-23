package com.example.geco.domains;

import java.time.LocalDateTime;

import com.example.geco.domains.Account.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityName;    
    private Long entityId;         

    private String action;         // e.g., "CREATE", "UPDATE", "DISABLE", "DELETE"
    private String email;       
    private Role performedByRole; 

    @Column(columnDefinition = "TEXT")
    private String oldValue;       // JSON or string representation of old state

    @Column(columnDefinition = "TEXT")
    private String newValue;       // JSON or string representation of new state
    
    @Builder.Default
    private LocalDateTime timestamp = null;  
    
    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}