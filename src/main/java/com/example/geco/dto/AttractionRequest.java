package com.example.geco.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttractionRequest {
	@NotNull(message = "Attraction's name is missing.")
	@Size(min = 1, message = "Attraction name must be at least have 1 character")
    private String attractionName;

	@NotNull(message = "Attraction's description is missing.")
	@Size(min = 5, message = "Attraction description must be at least have 5 characters")
	private String attractionDescription;
	
	@Size(min = 5, message = "Attraction fun fact must be at least have 5 characters")
	private String attractionFunFact;
}
