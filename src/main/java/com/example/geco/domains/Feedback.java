package com.example.geco.domains;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="feedback")
public class Feedback {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private int feedbackId;

    // A user can have as many feedback as many as his/her booking.
	@ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
	private Account userId;
	
	// Pertaining to the booking this feedback is related to.
	@ManyToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "bookingId")
	private Booking bookingId;
	
	private String category;
	private double stars;
	private String comment;
	private String suggestion;
}
