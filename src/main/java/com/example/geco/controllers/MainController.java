package com.example.geco.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

import com.example.geco.domains.AuditLog;
import com.example.geco.domains.Booking;
import com.example.geco.dto.AdminBookingRequest;
import com.example.geco.dto.AdminDashboardFinances;
import com.example.geco.dto.AdminDashboardStats;
import com.example.geco.dto.CalendarDay;
import com.example.geco.dto.ChartData;
import com.example.geco.dto.HomeStats;
import com.example.geco.dto.TrendsResponse;

@RestController
public class MainController extends AbstractController{
	
	// Functionalities in home page
	@GetMapping("/home")
	public ResponseEntity<HomeStats> home() {
		return ResponseEntity.ok(homepageService.getHomeStats());
    }
	
	// to implement:
	// logging for audit trail
	// configure access (secure endpoints)
	// payment function, either through gcash api or saving a screenshot of proof of payment
	
	// Add test/s for:
	// GET account
	// reordering faq
	// Dashboard bookings, finances and trends
	
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

	// functionalities of admin-dashboard bookings
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
	public ResponseEntity<List<ChartData>> displayDashboardFinancesYearlyRevenue(
			@RequestParam Integer startYear,
	        @RequestParam Integer endYear) {
		List<ChartData> revenue = bookingService.getYearlyRevenue(startYear, endYear);
		return new ResponseEntity<>(revenue, HttpStatus.OK);
	}
	
	@GetMapping("/dashboard/finances/revenue/monthly")
	public ResponseEntity<List<ChartData>> displayDashboardFinancesMonthlyRevenue(
			@RequestParam Integer year) {
		List<ChartData> revenue = bookingService.getMonthlyRevenue(year);
		return new ResponseEntity<>(revenue, HttpStatus.OK);
	}

	// functionalities of admin-dashboard trend
	@GetMapping("/dashboard/trends/yearly")
	public ResponseEntity<TrendsResponse> displayDashboardTrendsYearly(
			@RequestParam Integer startYear,
	        @RequestParam Integer endYear) {
		List<ChartData> yearlyBooking = adminDashboardService.getYearlyBookings(startYear, endYear);
		List<ChartData> yearlyVisitors = adminDashboardService.getYearlyVisitors(startYear, endYear);
		List<ChartData> yearlyAvailedPackages = adminDashboardService.getAvailedPackages(startYear, endYear);
		
		return ResponseEntity.ok(TrendsResponse.builder()
	            .bookings(yearlyBooking)
	            .visitors(yearlyVisitors)
	            .packages(yearlyAvailedPackages)
	            .build()
	            );
	}
	
	@GetMapping("/dashboard/trends/monthly")
	public ResponseEntity<TrendsResponse> displayDashboardTrendsMontly(
			@RequestParam Integer year) {
		List<ChartData> yearlyBooking = adminDashboardService.getMonthlyBookings(year);
		List<ChartData> yearlyVisitors = adminDashboardService.getMonthlyVisitors(year);
		List<ChartData> yearlyAvailedPackages = adminDashboardService.getAvailedPackages(year, year);
		
		return ResponseEntity.ok(TrendsResponse.builder()
	            .bookings(yearlyBooking)
	            .visitors(yearlyVisitors)
	            .packages(yearlyAvailedPackages)
	            .build()
	            );
	}
	
	@GetMapping("/dashboard/logs")
	public ResponseEntity<List<AuditLog>> getAuditLogs(
	        @RequestParam(required = false) String start,
	        @RequestParam(required = false) String end) {

	    LocalDateTime startTime = start != null ? LocalDateTime.parse(start) : null;
	    LocalDateTime endTime = end != null ? LocalDateTime.parse(end) : null;

	    List<AuditLog> logs = auditLogService.getLogsBetween(startTime, endTime);
	    return ResponseEntity.ok(logs);
	}


	// Admin-dashboard feedback categories
	// call GET /feedback-category for list of feedback categories
	// call GET /feedback for list of feedbacks w/ optional categoryId, startDate, endDate

	// functionalities of admin-dashboard Accounts
	// call GET /account/list/{role} for list accounts with certain roles

	// functionalities of admin-dashboard tour packages and package inclusions
	// call GET /package for tour packages
	// call GET /package-inclusion
	
	// functionalities of admin-dashboard attractions
	// call GET /attraction

	// functionalities of admin-dashboard frequently asked questions
	// call GET /faq
}
