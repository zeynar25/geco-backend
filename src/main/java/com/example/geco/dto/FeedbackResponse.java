package com.example.geco.dto;

import com.example.geco.domains.Account;
import com.example.geco.domains.Booking;
import com.example.geco.domains.Feedback.FeedbackStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponse {
	private int feedbackId;
	
	private Account account;
	private Booking booking;
	
	private String category;
	private double stars;
	private String comment;
	private String suggestion;
	private FeedbackStatus feedbackStatus;
}
