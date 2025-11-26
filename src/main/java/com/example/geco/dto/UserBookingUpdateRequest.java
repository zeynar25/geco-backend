package com.example.geco.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserBookingUpdateRequest {
	
	private LocalDate visitDate;
    private LocalTime visitTime;

    @Min(1)
    private Integer groupSize;

    private List<BookingInclusionRequest> bookingInclusionRequests;
}
