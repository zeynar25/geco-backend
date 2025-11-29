package com.example.geco.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailRequest {
    @Email(message = "Please provide a valid email")
    String email;
    @Size(min = 2, max = 50, message = "Surname must be between 2–50 characters")
    String surname;
    
    @Size(min = 2, max = 50, message = "First name must be between 2–50 characters")
    String firstName;
    
    @Pattern(
        regexp = "^(09\\d{9})?$",
        message = "Contact number must start with 09 and contain 11 digits"
    )
    String contactNumber;
}
