package com.example.geco.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class TourPackageRequest {

    @NotBlank(message = "Name is required and cannot be blank.")
    private String name;

    @NotBlank(message = "Description is required.")
    @Size(min = 10, message = "Description must be at least 10 characters long.")
    private String description;

    @NotNull(message = "Duration is required.")
    @Min(value = 30, message = "Duration must be at least 30 minutes.")
    private Integer duration;

    @NotNull(message = "Base price is required.")
    @Min(value = 0, message = "Base price must be 0 or higher.")
    private Integer basePrice;

    @NotEmpty(message = "At least one inclusion ID is required.")
    private List<Integer> inclusionIds;
}