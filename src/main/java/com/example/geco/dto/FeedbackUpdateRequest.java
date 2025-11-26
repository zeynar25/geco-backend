package com.example.geco.dto;

import com.example.geco.domains.Feedback.FeedbackStatus;

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
	private FeedbackStatus feedbackStatus;
}
