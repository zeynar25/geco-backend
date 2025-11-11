package com.example.geco.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.BookingInclusion;

@RestController
@RequestMapping("/booking-inclusion")
public class BookingInclusionController extends AbstractController{
	@PostMapping
	public ResponseEntity<BookingInclusion> addInclusion(@RequestBody BookingInclusion inclusion) {
		BookingInclusion savedInclusion = bookingInclusionService.addInclusion(inclusion);
        return new ResponseEntity<>(savedInclusion, HttpStatus.CREATED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<BookingInclusion> getInclusion(@PathVariable int id) {
		BookingInclusion inclusion = bookingInclusionService.getInclusion(id);
        return new ResponseEntity<>(inclusion, HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<List<BookingInclusion>> getAllInclusions() {
		List<BookingInclusion> inclusions = bookingInclusionService.getAllInclusions();
        return new ResponseEntity<>(inclusions, HttpStatus.OK);
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<BookingInclusion> updateInclusion(@PathVariable int id, @RequestBody BookingInclusion inclusion) {
		inclusion.setBookingInclusionId(id);
		BookingInclusion updatedInclusion = bookingInclusionService.updateInclusion(inclusion);
        return new ResponseEntity<>(updatedInclusion, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<BookingInclusion> deleteInclusion(@PathVariable int id) {
		bookingInclusionService.deleteInclusion(id);
	    return ResponseEntity.noContent().build();
	}
}
