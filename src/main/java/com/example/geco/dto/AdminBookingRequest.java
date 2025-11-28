package com.example.geco.dto;

import com.example.geco.domains.Booking.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminBookingRequest {
	String email;
	BookingStatus status;
}
