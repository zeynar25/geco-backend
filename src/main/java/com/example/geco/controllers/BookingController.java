package com.example.geco.controllers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.geco.domains.Booking;
import com.example.geco.domains.Booking.BookingStatus;
import com.example.geco.domains.Booking.PaymentMethod;
import com.example.geco.domains.Booking.PaymentStatus;
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
    	    summary = "Get all bookings of the logged-in user (paginated)",
    	    description = """
    	        Retrieves active bookings belonging to the authenticated user.
    	        Allows optional filtering by start and end date.
    	        Pagination is supported using 'page' and 'size'.
    	    """
	)
	@GetMapping("/me")
	public ResponseEntity<Page<Booking>> getAllMyBookings(
	        @Parameter(description = "Start date filter (optional)")
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

	        @Parameter(description = "End date filter (optional)")
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

	        @Parameter(description = "Page number (0-based)") 
	        @RequestParam(defaultValue = "0") int page,

	        @Parameter(description = "Page size") 
	        @RequestParam(defaultValue = "10") int size
	) {
	    Pageable pageable = PageRequest.of(page, size);
	    Page<Booking> bookings = bookingService.getMyBookingByDateRange(startDate, endDate, pageable);

	    return new ResponseEntity<>(bookings, HttpStatus.OK);
	}
    
    @Operation(
	    summary = "Get all Bookings",
	    description = "Retrieve a list of bookings. Can filter by account's accountId, date range, booking & payment statuses, payment method, and account email."
	)
	@GetMapping
	public ResponseEntity<Page<Booking>> getAllBookings(
	    @Parameter(description = "Filter by account ID")
	    @RequestParam(required = false) Integer accountId,

	    @Parameter(description = "Start date filter")
	    @RequestParam(required = false)
	    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

	    @Parameter(description = "End date filter")
	    @RequestParam(required = false)
	    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

	    @Parameter(description = "Booking Status")
	    @RequestParam(required = false) BookingStatus bookingStatus,

	    @Parameter(description = "Payment Status")
	    @RequestParam(required = false) PaymentStatus paymentStatus,

	    @Parameter(description = "Payment Method")
	    @RequestParam(required = false) PaymentMethod paymentMethod,

	    @Parameter(description = "Filter by account email (contains, case-insensitive)")
	    @RequestParam(required = false) String email,

	    @Parameter(description = "Page number")
	    @RequestParam(defaultValue = "0") int page,

	    @Parameter(description = "Page size")
	    @RequestParam(defaultValue = "10") int size
	) {
	    Pageable pageable = PageRequest.of(page, size);
	    Page<Booking> bookings = bookingService.getBookingByFilters(
	        accountId,
	        startDate,
	        endDate,
	        bookingStatus,
	        paymentStatus,
	        paymentMethod,
	        email,
	        pageable
	    );
	    return new ResponseEntity<>(bookings, HttpStatus.OK);
	}


    @Operation(
    	    summary = "Get all Active Bookings (paginated)",
    	    description = "Retrieve active bookings. Supports filters for account ID and date range."
    )
	@GetMapping("/active")
	public ResponseEntity<Page<Booking>> getAllActiveBookings(
	        @Parameter(description = "Filter by account ID (optional)")
	        @RequestParam(required = false) Integer accountId,

	        @Parameter(description = "Start date filter (optional)") 
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

	        @Parameter(description = "End date filter (optional)") 
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

	        @Parameter(description = "Page number (0-based)")
	        @RequestParam(defaultValue = "0") int page,

	        @Parameter(description = "Page size")
	        @RequestParam(defaultValue = "10") int size
	) {
	    Pageable pageable = PageRequest.of(page, size);
	    Page<Booking> bookings = bookingService.getActiveBookingByAccountAndDateRange(accountId, startDate, endDate, pageable);
	    return new ResponseEntity<>(bookings, HttpStatus.OK);
	}

    
    @Operation(
    	    summary = "Get all Inactive Bookings (paginated)",
    	    description = "Retrieve inactive bookings. Supports filters for account ID and date range."
	)
	@GetMapping("/inactive")
	public ResponseEntity<Page<Booking>> getAllInactiveBookings(
	        @Parameter(description = "Filter by account ID (optional)")
	        @RequestParam(required = false) Integer accountId,

	        @Parameter(description = "Start date filter (optional)") 
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

	        @Parameter(description = "End date filter (optional)") 
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

	        @Parameter(description = "Page number (0-based)")
	        @RequestParam(defaultValue = "0") int page,

	        @Parameter(description = "Page size")
	        @RequestParam(defaultValue = "10") int size
	) {
	    Pageable pageable = PageRequest.of(page, size);
	    Page<Booking> bookings = bookingService.getInactiveBookingByAccountAndDateRange(accountId, startDate, endDate, pageable);
	    return new ResponseEntity<>(bookings, HttpStatus.OK);
	}


    
    @Operation(
	    summary = "Update Booking (User)",
	    description = """
	        Allows a user to update their booking.
	        Editable fields: visitDate, visitTime, groupSize, inclusions.
	        Optionally accepts a proof-of-payment image file.
	    """
	)
	@PatchMapping(
	    value = "/{id}",
	    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity<Booking> updateBooking(
	    @Parameter(description = "ID of the booking to update")
	    @PathVariable int id,

	    @Parameter(description = "Booking update details (JSON)")
	    @RequestPart("data") @Valid UserBookingUpdateRequest request,

	    @Parameter(description = "Resubmitting proof of payment")
	    @RequestParam(value = "resubmit", defaultValue = "false") boolean resubmit,

	    @Parameter(description = "Proof of payment image (optional)")
	    @RequestPart(value = "proofOfPayment", required = false) MultipartFile proofOfPayment
	) {
	    Booking updatedBooking =
	        bookingService.updateBooking(id, request, resubmit, proofOfPayment);
	    return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
	}

    
    @Operation(
        summary = "Update Booking (Staff/Admin)",
        description = "Allows staff or admin to update the status or payment status of a booking."
    )
    @PatchMapping("/staff/{id}")
    public ResponseEntity<Booking> updateBookingByStaff(
        @Parameter(description = "ID of the booking to update") @PathVariable int id,
        @Parameter(description = "Booking update details") @RequestBody @Valid BookingUpdateRequest request
    ) {
        Booking updatedBooking = bookingService.updateBookingByStaff(id, request);
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
