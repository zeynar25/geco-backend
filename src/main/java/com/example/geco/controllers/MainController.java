package com.example.geco.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.Booking;
import com.example.geco.domains.Feedback.FeedbackStatus;
import com.example.geco.dto.AdminBookingRequest;
import com.example.geco.dto.AdminDashboardFinances;
import com.example.geco.dto.AdminDashboardStats;
import com.example.geco.dto.CalendarDay;
import com.example.geco.dto.HomeStats;
import com.example.geco.dto.MonthlyRevenue;

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
		AdminDashboardStats stats = new AdminDashboardStats(
				bookingService.getNumberOfBookingByMonth(LocalDate.now()),
				bookingService.getRevenueByMonth(LocalDate.now()),
				bookingService.getNumberOfPendingBookings(),
				feedbackService.getNumberOfNewFeedbacks(FeedbackStatus.NEW)
		);
		return new ResponseEntity<>(stats, HttpStatus.OK);
	}
	
	@GetMapping("/dashboard/bookings")
	public ResponseEntity<List<Booking>> displayDashboardBookings(@RequestBody AdminBookingRequest request) {
		List<Booking> bookings = adminDashboardService.getBookingByAdmin(request);
		return new ResponseEntity<>(bookings, HttpStatus.OK);
	}

	// functionalities of admin-dashboard financial
//	@GetMapping("/dashboard/finances")
//	public ResponseEntity<AdminDashboardFinances> displayDashboardFinances(
//			@RequestParam int year,
//	        @RequestParam int month) {
//		AdminDashboardFinances stats = adminDashboardService.getDashboardFinance(year, month);
//		
//		return new ResponseEntity<>(stats, HttpStatus.OK);
//	}
	
	@GetMapping("/dashboard/finances/revenue/{year}")
	public ResponseEntity<List<MonthlyRevenue>> displayDashboardFinancesYearlyRevenueTrend(@PathVariable Integer year) {
		List<MonthlyRevenue> revenue = bookingService.getRevenueByYear(year);
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

	// functionalities of admin-dashboard bookings
	@GetMapping("/dashboard/packages")
	public void displayDashboardPackages() {
		
	}

	// functionalities of admin-dashboard bookings
	@GetMapping("/dashboard/attractions")
	public void displayDashboardAttractions() {
		
	}
}
