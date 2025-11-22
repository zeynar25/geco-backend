package com.example.geco.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.Booking;
import com.example.geco.dto.AdminBookingRequest;
import com.example.geco.dto.AdminDashboardFinances;
import com.example.geco.dto.AdminDashboardStats;
import com.example.geco.dto.BookingRevenue;
import com.example.geco.dto.CalendarDay;
import com.example.geco.dto.HomeStats;

@RestController
public class MainController extends AbstractController{
	
	// Functionalities in home page
	@GetMapping("/home")
	public ResponseEntity<HomeStats> home() {
		return ResponseEntity.ok(homepageService.getHomeStats());
    }
	
	// to implement
	// payment function, either through gcash api or saving a screenshot of proof of payment
	
	@GetMapping("/calendar/{year}/{month}")
	public ResponseEntity<?> displayCalendar(@PathVariable int year, @PathVariable int month) {
		Map<Integer, CalendarDay> calendar = bookingService.getCalendar(year, month);
		return new ResponseEntity<>(calendar, HttpStatus.OK);
	}
	
	@GetMapping("/dashboard")
	public ResponseEntity<AdminDashboardStats> displayDashboard() {
		LocalDate now = LocalDate.now();
		AdminDashboardStats stats = adminDashboardService.getDashboardStats(now);
		return new ResponseEntity<>(stats, HttpStatus.OK);
	}
	
	@PostMapping("/dashboard/bookings")
	public ResponseEntity<List<Booking>> displayDashboardBookings(@RequestBody AdminBookingRequest request) {
		List<Booking> bookings = adminDashboardService.getBookingByAdmin(request);
		return new ResponseEntity<>(bookings, HttpStatus.OK);
	}

	// functionalities of admin-dashboard finances
	@GetMapping("/dashboard/finances")
	public ResponseEntity<AdminDashboardFinances> displayDashboardFinances(
			@RequestParam int year,
	        @RequestParam int month) {
		AdminDashboardFinances stats = adminDashboardService.getDashboardFinance(year, month);
		
		return new ResponseEntity<>(stats, HttpStatus.OK);
	}
	
	@GetMapping("/dashboard/finances/revenue/yearly")
	public ResponseEntity<List<BookingRevenue>> displayDashboardFinancesYearlyRevenue(
			@RequestParam Integer startYear,
	        @RequestParam Integer endYear) {
		List<BookingRevenue> revenue = bookingService.getYearlyRevenue(startYear, endYear);
		return new ResponseEntity<>(revenue, HttpStatus.OK);
	}
	
	@GetMapping("/dashboard/finances/revenue/monthly")
	public ResponseEntity<List<BookingRevenue>> displayDashboardFinancesMonthlyRevenue(
			@RequestParam Integer year) {
		List<BookingRevenue> revenue = bookingService.getMonthlyRevenue(year);
		return new ResponseEntity<>(revenue, HttpStatus.OK);
	}

	// functionalities of admin-dashboard trend
	@GetMapping("/dashboard/trends")
	public void displayDashboardTrends() {
		
	}

	// functionalities of admin-dashboard feedback
	@GetMapping("/dashboard/feedbacks")
	public void displayDashboardFeedbacks() {
		
	}
	
	// functionalities of admin-dashboard feedback categories
	@GetMapping("/dashboard/feedback-categories")
	public void displayDashboardFeedbackCategories() {
		
	}

	// functionalities of admin-dashboard Accounts
	@GetMapping("/dashboard/accounts")
	public void displayDashboardAccounts() {
		
	}

	// functionalities of admin-dashboard tour packages and package inclusions
	@GetMapping("/dashboard/packages")
	public void displayDashboardPackages() {
		
	}

	// functionalities of admin-dashboard attractions
	@GetMapping("/dashboard/attractions")
	public void displayDashboardAttractions() {
		
	}

	// functionalities of admin-dashboard frequently asked questions
	@GetMapping("/dashboard/faq")
	public void displayDashboardFaq() {
		
	}
}
