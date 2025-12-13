package com.example.geco.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.geco.domains.Booking.PaymentMethod;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {
	@NotNull(message = "Booking's account ID is missing.")
    private Integer accountId;
	
	@NotNull(message = "Booking's Tour package ID is missing.")
	private Integer tourPackageId;
	
    private List<BookingInclusionRequest> bookingInclusionRequests;
	
	@NotNull(message = "Booking's visit date is missing.")
	private LocalDate visitDate;
	
	@NotNull(message = "Booking's visit time is missing.")
	private LocalTime visitTime;
	
	private PaymentMethod paymentMethod;
	
	@NotNull
	@Min(value = 1, message = "Invalid Booking's group size.")
	private Integer groupSize;
}
