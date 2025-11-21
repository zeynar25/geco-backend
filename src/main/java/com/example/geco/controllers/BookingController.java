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

@RestController
@RequestMapping("/booking")
public class BookingController extends AbstractController{
	@PostMapping
	public ResponseEntity<Booking> addBooking(@RequestBody Booking booking) {
		Booking savedBooking = bookingService.addBooking(booking);
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Booking> getBooking(@PathVariable int id) {
		Booking attraction = bookingService.getBooking(id);
		return new ResponseEntity<>(attraction, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<Booking>> getAllBookings(
			@RequestParam(required = false) Integer accountId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		List<Booking> attractions = bookingService.getBookingByAccountAndDateRange(accountId, startDate, endDate);
		return new ResponseEntity<>(attractions, HttpStatus.OK);
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<Booking> updateBooking(@PathVariable int id, @RequestBody Booking booking) {
		if (booking.getAccount() == null &&
		    booking.getTourPackage() == null &&
		    booking.getInclusions() == null &&
		    booking.getVisitDate() == null &&
		    booking.getVisitTime() == null &&
		    booking.getGroupSize() == null &&
		    booking.getStatus() == null) {
		    throw new IllegalArgumentException("No fields provided to update for Booking.");
		}
		
		booking.setBookingId(id);
		Booking updatedAttraction  = bookingService.updateBooking(booking);
		return new ResponseEntity<>(updatedAttraction, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Booking> deleteBooking(@PathVariable int id) {
		bookingService.deleteBooking(id);
	    return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/statuses")
    public List<String> getBookingStatuses() {
        return Arrays.stream(Booking.BookingStatus.values())
             .map(Enum::name)
             .collect(Collectors.toList());
    }
}
