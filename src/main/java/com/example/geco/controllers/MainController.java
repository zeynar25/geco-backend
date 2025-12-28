package com.example.geco.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(
    name = "Main Controller", 
    description = "Provides endpoints for home statistics, calendar, admin dashboard, finances, trends, and audit logs"
)
public class MainController extends AbstractController {

    // -------------------------------
    // HOME ENDPOINTS
    // -------------------------------

    @Operation(
        summary = "Get Home Page Statistics",
        description = "Returns aggregated statistics for the home screen, including bookings, revenue, and visitors."
    )
    @GetMapping("/home")
    public ResponseEntity<HomeStats> home() {
        return ResponseEntity.ok(homepageService.getHomeStats());
    }


    // -------------------------------
    // CALENDAR ENDPOINTS
    // -------------------------------

    @Operation(
        summary = "Display Calendar View",
        description = "Returns a map of calendar days containing booking information for the specified year and month."
    )
    @GetMapping("/calendar/{year}/{month}")
    public ResponseEntity<Map<Integer, CalendarDay>> displayCalendar(
        @Parameter(description = "Year to display") @PathVariable int year,
        @Parameter(description = "Month to display") @PathVariable int month
    ) {
        Map<Integer, CalendarDay> calendar = bookingService.getCalendar(year, month);
        return new ResponseEntity<>(calendar, HttpStatus.OK);
    }

    @Operation(
        summary = "Get Calendar Monthly Statistics",
        description = "Returns aggregated statistics for all bookings within the specified year and month."
    )
    @GetMapping("/calendar/stats/{year}/{month}")
    public ResponseEntity<CalendarDay> displayCalendarStats(
        @Parameter(description = "Year of the calendar stats") @PathVariable int year,
        @Parameter(description = "Month of the calendar stats") @PathVariable int month
    ) {
        CalendarDay monthStats = bookingService.getCalendarStats(year, month);
        return new ResponseEntity<>(monthStats, HttpStatus.OK);
    }


    // -------------------------------
    // DASHBOARD STATISTICS
    // -------------------------------

    @Operation(
        summary = "Get Dashboard Statistics",
        description = "Returns admin dashboard statistics for today's date."
    )
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardStats> displayDashboard() {
        LocalDate now = LocalDate.now();
        AdminDashboardStats stats = adminDashboardService.getDashboardStats(now);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }


    @Operation(
        summary = "Get Dashboard Bookings",
        description = "Returns bookings filtered by date range, status, or admin search parameters."
    )
    @PostMapping("/dashboard/bookings")
    public ResponseEntity<List<Booking>> displayDashboardBookings(
        @Parameter(description = "Filter parameters for retrieving bookings") @RequestBody AdminBookingRequest request
    ) {
        List<Booking> bookings = adminDashboardService.getBookingByAdmin(request);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }


    // -------------------------------
    // FINANCES
    // -------------------------------

    @Operation(
        summary = "Get Monthly Financial Statistics",
        description = "Returns total revenue, expenses, and computed balance for the given year and month."
    )
    @GetMapping("/dashboard/finances")
    public ResponseEntity<AdminDashboardFinances> displayDashboardFinances(
        @Parameter(description = "Year to retrieve") @RequestParam(required = false) Integer year,
        @Parameter(description = "Month to retrieve") @RequestParam(required = false) Integer month
    ) {
        AdminDashboardFinances stats = adminDashboardService.getDashboardFinance(year, month);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @Operation(
        summary = "Get Yearly Revenue Data",
        description = "Returns yearly revenue chart points between startYear and endYear."
    )
    @GetMapping("/dashboard/finances/revenue/yearly")
    public ResponseEntity<List<ChartData>> displayDashboardFinancesYearlyRevenue(
        @Parameter(description = "Start year for revenue data") @RequestParam Integer startYear,
        @Parameter(description = "End year for revenue data") @RequestParam Integer endYear
    ) {
        List<ChartData> revenue = bookingService.getYearlyRevenue(startYear, endYear);
        return new ResponseEntity<>(revenue, HttpStatus.OK);
    }

    @Operation(
        summary = "Get Monthly Revenue Data",
        description = "Returns monthly revenue chart points for a specified year."
    )
    @GetMapping("/dashboard/finances/revenue/monthly")
    public ResponseEntity<List<ChartData>> displayDashboardFinancesMonthlyRevenue(
        @Parameter(description = "Year to retrieve") @RequestParam Integer year
    ) {
        List<ChartData> revenue = bookingService.getMonthlyRevenue(year);
        return new ResponseEntity<>(revenue, HttpStatus.OK);
    }


    // -------------------------------
    // TRENDS (BOOKINGS, VISITORS, PACKAGES)
    // -------------------------------

    @Operation(
        summary = "Get Yearly Trends",
        description = "Returns yearly chart data for bookings, visitors, and packages availed."
    )
    @GetMapping("/dashboard/trends/yearly")
    public ResponseEntity<TrendsResponse> displayDashboardTrendsYearly(
        @Parameter(description = "Start year") @RequestParam Integer startYear,
        @Parameter(description = "End year") @RequestParam Integer endYear
    ) {
        List<ChartData> yearlyBooking = adminDashboardService.getYearlyBookings(startYear, endYear);
        List<ChartData> yearlyVisitors = adminDashboardService.getYearlyVisitors(startYear, endYear);
        List<ChartData> yearlyAvailedPackages = adminDashboardService.getAvailedPackages(startYear, endYear);

        return ResponseEntity.ok(TrendsResponse.builder()
                .bookings(yearlyBooking)
                .visitors(yearlyVisitors)
                .packages(yearlyAvailedPackages)
                .build());
    }

    @Operation(
        summary = "Get Monthly Trends",
        description = "Returns monthly chart data for bookings, visitors, and availed packages for a given year."
    )
    @GetMapping("/dashboard/trends/monthly")
    public ResponseEntity<TrendsResponse> displayDashboardTrendsMonthly(
        @Parameter(description = "Year to retrieve monthly trends") @RequestParam Integer year
    ) {
        List<ChartData> monthlyBooking = adminDashboardService.getMonthlyBookings(year);
        List<ChartData> monthlyVisitors = adminDashboardService.getMonthlyVisitors(year);
        List<ChartData> monthlyAvailedPackages = adminDashboardService.getAvailedPackages(year, year);

        return ResponseEntity.ok(TrendsResponse.builder()
                .bookings(monthlyBooking)
                .visitors(monthlyVisitors)
                .packages(monthlyAvailedPackages)
                .build());
    }


    // -------------------------------
    // AUDIT LOGS
    // -------------------------------

    @Operation(
    	    summary = "Get Audit Logs",
    	    description = "Retrieve logs filtered by optional date range, entity name, or action type."
    	)
    	@GetMapping("/dashboard/logs")
    	public ResponseEntity<Page<AuditLog>> getAuditLogs(
    	    @Parameter(description = "Start datetime in ISO format (yyyy-MM-ddTHH:mm:ss)")
    	    @RequestParam(required = false) String start,
    	    @Parameter(description = "End datetime in ISO format (yyyy-MM-ddTHH:mm:ss)")
    	    @RequestParam(required = false) String end,
    	    @Parameter(description = "Filter by entity name")
    	    @RequestParam(required = false) String entityName,
    	    @Parameter(description = "Filter by log action type")
    	    @RequestParam(required = false) LogAction action,
    	    @Parameter(description = "Page number (0-based)")
    	    @RequestParam(defaultValue = "0") int page,
    	    @Parameter(description = "Page size")
    	    @RequestParam(defaultValue = "20") int size
    	) {
    	    LocalDateTime startTime = null;
    	    LocalDateTime endTime = null;

    	    try {
    	        if (start != null) {
    	            startTime = LocalDateTime.parse(start);
    	        }
    	        if (end != null) {
    	            endTime = LocalDateTime.parse(end);
    	        }
    	    } catch (DateTimeParseException ex) {
    	        throw new IllegalArgumentException(
    	            "Invalid datetime format. Use yyyy-MM-ddTHH:mm:ss", ex
    	        );
    	    }

    	    Page<AuditLog> logs =
    	            auditLogService.getLogs(startTime, endTime, entityName, action, page, size);

    	    return ResponseEntity.ok(logs);
    	}
}
