package com.example.geco.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingInclusionRequest {
	@NotNull
	private Integer inclusionId;
    
    // How many people in the group will use this add-on.
	@NotNull
	@Min(1)
    private Integer quantity;      
}
