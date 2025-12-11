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
import com.example.geco.domains.AuditLog.LogAction;
import com.example.geco.domains.Booking;
import com.example.geco.dto.AdminBookingRequest;
import com.example.geco.dto.AdminDashboardFinances;
import com.example.geco.dto.AdminDashboardStats;
import com.example.geco.dto.CalendarDay;
import com.example.geco.dto.ChartData;
import com.example.geco.dto.HomeStats;
import com.example.geco.dto.TrendsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Main", description = "Main application endpoints for home, dashboard, calendar, bookings, finances, trends, and logs")
public class MainController extends AbstractController {

    @GetMapping("/home")
    @Operation(summary = "Get home page statistics", description = "Returns aggregated statistics to display on the home page")
    public ResponseEntity<HomeStats> home() {
        return ResponseEntity.ok(homepageService.getHomeStats());
    }

    @GetMapping("/calendar/{year}/{month}")
    @Operation(summary = "Display calendar for a given month and year", description = "Returns a map of calendar days and bookings for the specified year and month")
    public ResponseEntity<?> displayCalendar(@PathVariable int year, @PathVariable int month) {
        Map<Integer, CalendarDay> calendar = bookingService.getCalendar(year, month);
        return new ResponseEntity<>(calendar, HttpStatus.OK);
    }
    
    @GetMapping("/calendar/stats/{year}/{month}")
    @Operation(summary = "Display calendar for a given month and year", description = "Returns a map of calendar days and bookings for the specified year and month")
    public ResponseEntity<?> displayCalendarStats(@PathVariable int year, @PathVariable int month) {
        CalendarDay monthStats = bookingService.getCalendarStats(year, month);
        return new ResponseEntity<>(monthStats, HttpStatus.OK);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Display dashboard statistics", description = "Returns admin dashboard statistics for the current day")
    public ResponseEntity<AdminDashboardStats> displayDashboard() {
        LocalDate now = LocalDate.now();
        AdminDashboardStats stats = adminDashboardService.getDashboardStats(now);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @PostMapping("/dashboard/bookings")
    @Operation(summary = "Get dashboard bookings", description = "Returns a list of bookings based on admin request filters")
    public ResponseEntity<List<Booking>> displayDashboardBookings(@RequestBody AdminBookingRequest request) {
        List<Booking> bookings = adminDashboardService.getBookingByAdmin(request);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/dashboard/finances")
    @Operation(summary = "Get dashboard financial statistics", description = "Returns monthly financial statistics for the specified year and month")
    public ResponseEntity<AdminDashboardFinances> displayDashboardFinances(
            @RequestParam int year,
            @RequestParam int month) {
        AdminDashboardFinances stats = adminDashboardService.getDashboardFinance(year, month);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @GetMapping("/dashboard/finances/revenue/yearly")
    @Operation(summary = "Get yearly revenue data", description = "Returns chart data of revenue between startYear and endYear")
    public ResponseEntity<List<ChartData>> displayDashboardFinancesYearlyRevenue(
            @RequestParam Integer startYear,
            @RequestParam Integer endYear) {
        List<ChartData> revenue = bookingService.getYearlyRevenue(startYear, endYear);
        return new ResponseEntity<>(revenue, HttpStatus.OK);
    }

    @GetMapping("/dashboard/finances/revenue/monthly")
    @Operation(summary = "Get monthly revenue data", description = "Returns chart data of revenue for the specified year")
    public ResponseEntity<List<ChartData>> displayDashboardFinancesMonthlyRevenue(
            @RequestParam Integer year) {
        List<ChartData> revenue = bookingService.getMonthlyRevenue(year);
        return new ResponseEntity<>(revenue, HttpStatus.OK);
    }

    @GetMapping("/dashboard/trends/yearly")
    @Operation(summary = "Get yearly trends", description = "Returns yearly chart data for bookings, visitors, and availed packages")
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
                .build());
    }

    @GetMapping("/dashboard/trends/monthly")
    @Operation(summary = "Get monthly trends", description = "Returns monthly chart data for bookings, visitors, and availed packages for the specified year")
    public ResponseEntity<TrendsResponse> displayDashboardTrendsMontly(
            @RequestParam Integer year) {
        List<ChartData> monthlyBooking = adminDashboardService.getMonthlyBookings(year);
        List<ChartData> monthlyVisitors = adminDashboardService.getMonthlyVisitors(year);
        List<ChartData> monthlyAvailedPackages = adminDashboardService.getAvailedPackages(year, year);

        return ResponseEntity.ok(TrendsResponse.builder()
                .bookings(monthlyBooking)
                .visitors(monthlyVisitors)
                .packages(monthlyAvailedPackages)
                .build());
    }

    @GetMapping("/dashboard/logs")
    @Operation(summary = "Get audit logs", description = "Returns a list of audit logs filtered by optional start/end dates, entity name, or action type")
    public ResponseEntity<List<AuditLog>> getAuditLogs(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) String entityName,
            @RequestParam(required = false) LogAction action) {

        LocalDateTime startTime = start != null ? LocalDateTime.parse(start) : null;
        LocalDateTime endTime = end != null ? LocalDateTime.parse(end) : null;

        List<AuditLog> logs = auditLogService.getLogs(startTime, endTime, entityName, action);
        return ResponseEntity.ok(logs);
    }

    // Placeholder endpoints (no implementation shown) for admin dashboard features
    // Feedback categories, accounts, tour packages, package inclusions, attractions, FAQ
    // Use GET endpoints as described in the original comments
}
