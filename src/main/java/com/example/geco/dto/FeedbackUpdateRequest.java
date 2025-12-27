package com.example.geco.dto;

import com.example.geco.domains.Feedback.FeedbackStatus;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackUpdateRequest {
    @Size(min = 10, message = "Reply must be at least 10 characters")
	private String staffReply;
	private FeedbackStatus feedbackStatus;
}
