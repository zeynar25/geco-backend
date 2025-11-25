package com.example.geco.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.geco.services.AccountService;
import com.example.geco.services.AdminDashboardService;
import com.example.geco.services.AttractionService;
import com.example.geco.services.AuditLogService;
import com.example.geco.services.AuthService;
import com.example.geco.services.BookingInclusionService;
import com.example.geco.services.BookingService;
import com.example.geco.services.FaqService;
import com.example.geco.services.FeedbackCategoryService;
import com.example.geco.services.FeedbackService;
import com.example.geco.services.HomepageService;
import com.example.geco.services.JwtService;
import com.example.geco.services.PackageInclusionService;
import com.example.geco.services.TokenBlacklistService;
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
	
	@Autowired
	protected PackageInclusionService packageInclusionService;
	
	@Autowired
	protected BookingInclusionService bookingInclusionService;

    @Autowired
    protected AuthService authService;
    
    @Autowired
    protected JwtService jwtService;
    
    @Autowired
    protected TokenBlacklistService tokenBlacklistService;
    
    @Autowired
    protected HomepageService homepageService;
    
    @Autowired
    protected AdminDashboardService adminDashboardService;
    
    @Autowired
    protected AuditLogService auditLogService;
}
