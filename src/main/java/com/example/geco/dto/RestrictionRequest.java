package com.example.geco.dto;

import jakarta.validation.constraints.Min;
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
public class RestrictionRequest {
	@NotNull(message = "Restriction name is missing.")
    @Size(min = 3, message = "Restriction name must at least be 3 characters")
	private String name;
	
	@NotNull(message = "Restriction value is missing.")
	@Min(value = 0, message = "Restriction value must at least be 0.")
	private Integer value;
}
