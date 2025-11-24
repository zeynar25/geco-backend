package com.example.geco.domains;

import java.time.LocalDateTime;

import com.example.geco.domains.Account.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	public enum LogAction {
	    CREATE,
	    UPDATE,
	    DISABLE,
	    DELETE,
	    RESTORE
	}
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityName;    
    private Long entityId;         

    @Enumerated(EnumType.STRING)
    private LogAction action;         // e.g., "CREATE", "UPDATE", "DISABLE", "DELETE"
    
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