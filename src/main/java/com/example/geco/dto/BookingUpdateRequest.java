package com.example.geco.dto;

import com.example.geco.domains.Booking.BookingStatus;
import com.example.geco.domains.Booking.PaymentMethod;
import com.example.geco.domains.Booking.PaymentStatus;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true) 
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BookingUpdateRequest extends UserBookingUpdateRequest{
	private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    
    private String staffReply;
}
