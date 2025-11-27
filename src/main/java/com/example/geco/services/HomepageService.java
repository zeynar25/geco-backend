package com.example.geco.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.geco.dto.HomeStats;

@Service
public class HomepageService {
	@Autowired
	AttractionService attractionService;
	
	@Autowired
	BookingService bookingService;
	
	@Autowired
	FeedbackService feedbackService;

	public HomeStats getHomeStats() {
		return new HomeStats(
				attractionService.getAttractionsNumber(),
				bookingService.getAverageVisitor("month"),
				feedbackService.getAverageRating());
	}

}
