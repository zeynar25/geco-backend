package com.example.geco.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class FeedbackRequest {
    @NotNull(message = "Booking ID is required")
    private Integer bookingId;

    @NotNull(message = "Feedback Category ID is required")
    private Integer categoryId;

    @Min(value = 0, message = "Stars must be at least 0")
    @Max(value = 5, message = "Stars cannot exceed 5")
    private Double stars;

    @NotBlank(message = "Comment is required")
    @Size(min = 10, message = "Feedback comment must be at least 10 characters long.")
    private String comment;

    private String suggestion; // Optional
}