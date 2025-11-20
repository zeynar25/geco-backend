package com.example.geco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
	Integer accountId;
	String role;
	String passwordNotice;
	
	Integer detailId;
    String surname;
    String firstName;
    String email;
    String contactNumber;
}
