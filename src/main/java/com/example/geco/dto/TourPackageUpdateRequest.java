package com.example.geco.dto;

import java.util.List;

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
public class TourPackageUpdateRequest {

    private String name;

    @Size(min = 10, message = "Description must be at least 10 characters long.")
    private String description;

    private Integer duration;

    @Min(value = 0, message = "Base price must be 0 or higher.")
    private Double basePrice;
    
    @Min(value = 0, message = "Price per person must be 0 or higher.")
    private Double pricePerPerson;
    
    @Min(value = 1, message = "Minimum person must be at least be 1.")
    private Integer minPerson;

    @Min(value = 1, message = "Max person must be at least be 1.")
    private Integer maxPerson;
    
    @Size(min = 1, message = "Notes must be at least be 1 character long.")
    private String notes;

    private List<Integer> inclusionIds;
}