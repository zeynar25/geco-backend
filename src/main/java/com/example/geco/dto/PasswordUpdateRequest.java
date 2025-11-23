package com.example.geco.dto;

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
public class PasswordUpdateRequest {
	Integer accountId;
	
	@NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
	String password;
}
