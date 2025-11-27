package com.example.geco.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserFeedbackUpdateRequest {

    @Min(value = 0, message = "Stars must be at least 0")
    @Max(value = 5, message = "Stars cannot exceed 5")
    private Double stars; // optional

    @Size(min = 10, message = "Comment must be at least 10 characters")
    private String comment; 

    private String suggestion;
}
