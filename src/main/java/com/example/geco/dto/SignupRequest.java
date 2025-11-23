package com.example.geco.dto;

import com.example.geco.domains.Account.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {
	@NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
	String email;
	
	@NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
	String password;
	
	Role role;
}
