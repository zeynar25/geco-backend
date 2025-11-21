package com.example.geco.controllers;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.Feedback.FeedbackStatus;
import com.example.geco.dto.CalendarDay;
import com.example.geco.dto.DashboardStats;
import com.example.geco.dto.HomeStats;

@RestController
public class MainController extends AbstractController{
	
	// Functionalities in home page
	@GetMapping("/home")
	public HomeStats home() {
		HomeStats stats = new HomeStats(
				attractionService.getAttractionsNumber(),
				bookingService.getAverageVisitor("Month"),
				feedbackService.getAverageRating()
		);
		return stats;
    }
	
	// to implement
	// payment function, either through gcash api or saving a screenshot of proof of payment
	
	@GetMapping("/calendar/{year}/{month}")
	public ResponseEntity<?> displayCalendar(@PathVariable int year, @PathVariable int month) {
		Map<Integer, CalendarDay> calendar = bookingService.getCalendar(year, month);
		return new ResponseEntity<>(calendar, HttpStatus.OK);
	}
	
	@GetMapping("/dashboard")
	public DashboardStats displayDashboard() {
		DashboardStats stats = new DashboardStats(
				bookingService.getNumberOfBookingByMonth(LocalDate.now()),
				bookingService.getRevenueByMonth(LocalDate.now()),
				bookingService.getNumberOfPendingBookings(),
				feedbackService.getNumberOfNewFeedbacks(FeedbackStatus.NEW)
		);
		return stats;
	}
	
	// functionalities of admin-dashboard bookings
	@GetMapping("/dashboard/bookings")
	public void displayDashboardBookings() {
		
		
	}

	// functionalities of admin-dashboard financial
	@GetMapping("/dashboard/finances")
	public void displayDashboardFinances() {
		
	}

	// functionalities of admin-dashboard trend
	@GetMapping("/dashboard/trends")
	public void displayDashboardTrends() {
		
	}

	// functionalities of admin-dashboard feedback
	@GetMapping("/dashboard/feedbacks")
	public void displayDashboardFeedbacks() {
		
	}

	// functionalities of admin-dashboard bookings
	@GetMapping("/dashboard/packages")
	public void displayDashboardPackages() {
		
	}

	// functionalities of admin-dashboard bookings
	@GetMapping("/dashboard/attractions")
	public void displayDashboardAttractions() {
		
	}
}
