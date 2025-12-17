package com.example.geco.domains;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="feedback")
public class Feedback {
	public enum FeedbackStatus {
	    NEW, VIEWED
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Integer feedbackId;

    // A user can have as many feedback as many as his/her booking.
	@ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "accountId")
	private Account account;
	
	// Pertaining to the booking this feedback is related to.
	@ManyToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "bookingId")
	private Booking booking;

	// Pertaining to the feedback category this feedback is related to.
	@ManyToOne
	@JoinColumn(name = "category_id", referencedColumnName = "feedbackCategoryId")
	private FeedbackCategory category;
	
	private Double stars;
	private String comment;
	private String suggestion;
	
	private String staffReply;
	
	@Enumerated(EnumType.STRING)
	private FeedbackStatus feedbackStatus;
	
	@Builder.Default
	private boolean isActive = true;
}
