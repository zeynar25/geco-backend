package com.example.geco.dto;

import com.example.geco.domains.Feedback.FeedbackStatus;

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
public class FeedbackUpdateRequest extends UserFeedbackUpdateRequest{
    @Size(min = 10, message = "Reply must be at least 10 characters")
	private String staffReply;
	private FeedbackStatus feedbackStatus;
}
