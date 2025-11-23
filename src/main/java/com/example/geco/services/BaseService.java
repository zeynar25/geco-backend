package com.example.geco.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.geco.domains.Account;
import com.example.geco.domains.Account.Role;
import com.example.geco.exceptions.AccessDeniedException;

public abstract class BaseService {
	@Autowired
	AuditLogService auditLogService;
	
	
	protected Account getLoggedAccount() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth == null || !(auth.getPrincipal() instanceof Account)) {
	        throw new AccessDeniedException("No authenticated user found.");
	    }
	    return (Account) auth.getPrincipal();
	}

	protected String getLoggedAccountEmail() {
		Account account = getLoggedAccount();
        return account.getDetail() != null ? account.getDetail().getEmail() : null;
	}

	protected Role getLoggedAccountRole() {
	    return getLoggedAccount().getRole();
	}
	
	protected void logIfAuthenticatedStaffOrAdmin(String entity, 
			Long entityId, 
			String action, 
			Object prevVal, 
			Object currVal) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // Return early if no authenticated user
	    if (auth == null || !(auth.getPrincipal() instanceof Account)) {
	        return;
	    }
	    
		Role role = getLoggedAccountRole();
		
		if (role.equals(Role.GUEST)) {
			return;
		}
		
		auditLogService.logAction(
				entity,
				(long) entityId,
				action,
				prevVal,
				currVal,
				getLoggedAccountEmail(),
				role);
	}
	
	protected void logIfStaffOrAdmin(String entity, 
			Long entityId, 
			String action, 
			Object prevVal, 
			Object currVal) {
		Role role = getLoggedAccountRole();
		
		if (role == Role.GUEST) return;
		
		auditLogService.logAction(
				entity,
				entityId,
				action,
				prevVal,
				currVal,
				getLoggedAccountEmail(),
				role);
	}
}
