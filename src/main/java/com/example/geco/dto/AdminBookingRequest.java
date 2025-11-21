package com.example.geco.dto;

import com.example.geco.domains.Booking.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminBookingRequest {
	String name;
	BookingStatus status;
}
