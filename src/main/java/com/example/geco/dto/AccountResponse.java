package com.example.geco.dto;

import com.example.geco.domains.Account.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
	public enum PasswordStatus {
	    UNCHANGED,
	    CHANGED_SUCCESSFULLY,
	    RESET_SUCCESSFULLY
	}
	
	Integer accountId;
	Role role;
	PasswordStatus passwordStatus;
	
	Integer detailId;
    String surname;
    String firstName;
    String email;
    String contactNumber;
    
    Boolean isActive;
}
