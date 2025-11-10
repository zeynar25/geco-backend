package com.example.geco.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.geco.services.AccountService;
import com.example.geco.services.AttractionService;
import com.example.geco.services.BookingService;
import com.example.geco.services.FaqService;
import com.example.geco.services.FeedbackCategoryService;
import com.example.geco.services.FeedbackService;
import com.example.geco.services.TourPackageService;

public abstract class AbstractController {
	@Autowired
	protected AccountService accountService;
	
	@Autowired
	protected AttractionService attractionService;
	
	@Autowired
	protected BookingService bookingService;
	
	@Autowired
	protected FeedbackService feedbackService;
	
	@Autowired
	protected FeedbackCategoryService feedbackCategoryService;
	
	@Autowired
	protected FaqService faqService;

	@Autowired
	protected TourPackageService tourPackageService;
}
