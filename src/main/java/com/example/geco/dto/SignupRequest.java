package com.example.geco.dto;

import com.example.geco.domains.Account;
import com.example.geco.domains.UserDetail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
	private Account account;
    private UserDetail userDetail;
}
