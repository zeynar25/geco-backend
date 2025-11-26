package com.example.geco.controllers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.Booking;
import com.example.geco.dto.BookingRequest;
import com.example.geco.dto.BookingUpdateRequest;
import com.example.geco.dto.UserBookingUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/booking")
@Tag(name = "Booking Controller", description = "Manage bookings, including CRUD operations, soft delete, restore, and status updates")
public class BookingController extends AbstractController {

    @Operation(
        summary = "Add a new Booking",
        description = "Create a new booking for a tour package. Includes account, package, visit date/time, group size, and optional inclusions."
    )
    @PostMapping
    public ResponseEntity<Booking> addBooking(
        @Parameter(description = "Booking details to create") @RequestBody @Valid BookingRequest request
    ) {
        Booking savedBooking = bookingService.addBooking(request);
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }

    
    @Operation(
        summary = "Get Booking by ID",
        description = "Retrieve a single booking by its unique ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBooking(
        @Parameter(description = "ID of the booking to retrieve") @PathVariable int id
    ) {
        Booking booking = bookingService.getBooking(id);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @Operation(
    	    summary = "Get all bookings of the logged-in user",
    	    description = """
    	        Retrieves all active bookings belonging to the authenticated user.
    	        You may optionally filter the results by providing a start date, end date, or both.
    	        If no dates are provided, all active bookings of the user will be returned.
    	    """
	)
    @GetMapping("/me")
    public ResponseEntity<List<Booking>> getAllMyBookings(
        @Parameter(description = "Start date for filtering bookings") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Parameter(description = "End date for filtering bookings") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<Booking> bookings = bookingService.getMyBookingByDateRange(startDate, endDate);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
    
    @Operation(
        summary = "Get all Bookings",
        description = "Retrieve a list of bookings. Can filter by account ID and/or date range."
    )
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings(
        @Parameter(description = "Filter bookings by account ID") @RequestParam(required = false) Integer accountId,
        @Parameter(description = "Start date for filtering bookings") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Parameter(description = "End date for filtering bookings") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<Booking> bookings = bookingService.getBookingByAccountAndDateRange(accountId, startDate, endDate);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @Operation(
        summary = "Get all Active Bookings",
        description = "Retrieve all bookings that are currently active. Can filter by account ID and/or date range."
    )
    @GetMapping("/active")
    public ResponseEntity<List<Booking>> getAllActiveBookings(
        @Parameter(description = "Filter bookings by account ID") @RequestParam(required = false) Integer accountId,
        @Parameter(description = "Start date for filtering bookings") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Parameter(description = "End date for filtering bookings") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<Booking> bookings = bookingService.getActiveBookingByAccountAndDateRange(accountId, startDate, endDate);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    
    @Operation(
        summary = "Get all Inactive Bookings",
        description = "Retrieve all bookings that are inactive. Can filter by account ID and/or date range."
    )
    @GetMapping("/inactive")
    public ResponseEntity<List<Booking>> getAllInactiveBookings(
        @Parameter(description = "Filter bookings by account ID") @RequestParam(required = false) Integer accountId,
        @Parameter(description = "Start date for filtering bookings") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Parameter(description = "End date for filtering bookings") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<Booking> bookings = bookingService.getInactiveBookingByAccountAndDateRange(accountId, startDate, endDate);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    
    @Operation(
        summary = "Update Booking (User)",
        description = "Allows a user to update their booking. Only certain fields are editable by the user."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(
        @Parameter(description = "ID of the booking to update") @PathVariable int id,
        @Parameter(description = "Booking update details") @RequestBody @Valid UserBookingUpdateRequest request
    ) {
        Booking updatedBooking = bookingService.updateBooking(id, request);
        return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
    }

    
    @Operation(
        summary = "Update Booking Status (Staff/Admin)",
        description = "Allows staff or admin to update the status or payment status of a booking."
    )
    @PatchMapping("/staff/{id}")
    public ResponseEntity<Booking> updateBookingStatus(
        @Parameter(description = "ID of the booking to update") @PathVariable int id,
        @Parameter(description = "Booking status update details") @RequestBody @Valid BookingUpdateRequest request
    ) {
        Booking updatedBooking = bookingService.updateBookingByAdmin(id, request);
        return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
    }

    
    @Operation(
        summary = "Soft Delete Booking",
        description = "Marks a booking as inactive (soft delete) without removing it from the database."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(
        @Parameter(description = "ID of the booking to delete") @PathVariable int id
    ) {
        bookingService.softDeleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    
    @Operation(
        summary = "Restore Booking",
        description = "Restores a previously soft-deleted booking."
    )
    @PatchMapping("/restore/{id}")
    public ResponseEntity<Void> restoreBooking(
        @Parameter(description = "ID of the booking to restore") @PathVariable int id
    ) {
        bookingService.restoreBooking(id);
        return ResponseEntity.noContent().build();
    }

    
    @Operation(
        summary = "Get Booking Statuses",
        description = "Retrieve all possible booking statuses (e.g., PENDING, APPROVED, CANCELLED, COMPLETED)."
    )
    @GetMapping("/statuses")
    public List<String> getBookingStatuses() {
        return Arrays.stream(Booking.BookingStatus.values())
                     .map(Enum::name)
                     .collect(Collectors.toList());
    }
}
