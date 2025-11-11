package com.example.geco.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.geco.repositories.AccountRepository;
import com.example.geco.repositories.AttractionRepository;
import com.example.geco.repositories.FaqRepository;
import com.example.geco.repositories.FeedbackCategoryRepository;
import com.example.geco.repositories.PackageInclusionRepository;
import com.example.geco.repositories.TourPackageRepository;
import com.example.geco.repositories.UserDetailRepository;
import com.example.geco.services.AccountService;
import com.example.geco.services.AttractionService;
import com.example.geco.services.BookingInclusionService;
import com.example.geco.services.BookingService;
import com.example.geco.services.FaqService;
import com.example.geco.services.FeedbackCategoryService;
import com.example.geco.services.PackageInclusionService;
import com.example.geco.services.TourPackageService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractControllerTest {
	@Autowired
	protected MockMvc mockMvc;
	
	@Autowired
	protected ObjectMapper objectMapper;
	
	@Autowired
	protected AccountService accountService;
	
	@Autowired
	protected AttractionService attractionService;

	@Autowired
	protected FeedbackCategoryService feedbackCategoryService;
	
	@Autowired
	protected FaqService faqService;

	@Autowired
	protected TourPackageService tourPackageService;
	
	@Autowired
	protected PackageInclusionService packageInclusionService;
	
	@Autowired
	protected BookingService bookingService;
	
	@Autowired
	protected BookingInclusionService bookingInclusionService;
	
	@Autowired
	protected AccountRepository accountRepository;
	
	@Autowired
	protected UserDetailRepository userDetailRepository;
	
	@Autowired
	protected AttractionRepository attractionRepository;
	
	@Autowired
	protected FeedbackCategoryRepository feedbackCategoryRepository;
	
	@Autowired
	protected FaqRepository faqRepository;

	@Autowired
	protected TourPackageRepository tourPackageRepository;

	@Autowired
	protected PackageInclusionRepository packageInclusionRepository;
}
