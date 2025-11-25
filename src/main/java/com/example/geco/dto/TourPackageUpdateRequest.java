package com.example.geco.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
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

    @Min(value = 30, message = "Duration must be at least 30 minutes.")
    private Integer duration;

    @Min(value = 0, message = "Base price must be 0 or higher.")
    private Integer basePrice;

    private List<Integer> inclusionIds;
}