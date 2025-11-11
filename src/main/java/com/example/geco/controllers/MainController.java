package com.example.geco.controllers;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.Account;

@RestController
public class MainController extends AbstractController{
	
	// Functionalities in home page
	@GetMapping
	public HashMap<String, Double> home() {
		HashMap<String, Double> stats = new HashMap<>();
		Double attractions = attractionService.getAttractionsNumber();
		Double monthlyVisitors = bookingService.getAverageVisitor("Month");
		Double avgRating = feedbackService.getAverageRating();
		
		stats.put("attractionsNumber", attractions);
		stats.put("monthlyVisitors", monthlyVisitors);
		stats.put("averageRating", avgRating);
		
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
	
	// to implement
	@PostMapping("/login")
	public void login(@RequestBody Account account) {
	}
	
	// to implement
	@PostMapping("/logout")
	public void logout(@RequestBody Account account) {
		
	}
	
	// to implement
	// payment function, either through gcash api or saving a screenshot of proof of payment
	
	// to implement
	@GetMapping("/calendar/{year}/{month}")
	public ResponseEntity<?> displayCalendar(@PathVariable int year, @PathVariable int month) {
		return new ResponseEntity<>("calendar", HttpStatus.OK);
	}
}
