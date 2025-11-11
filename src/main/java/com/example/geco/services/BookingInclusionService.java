package com.example.geco.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.geco.domains.Booking;
import com.example.geco.domains.BookingInclusion;
import com.example.geco.repositories.BookingInclusionRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BookingInclusionService {
	@Autowired
	BookingInclusionRepository inclusionRepository;
	
	public BookingInclusion addInclusion(BookingInclusion inclusion) {
		if (inclusion.getBooking() == null) {
	        throw new IllegalArgumentException("Booking Inclusion's booking is missing.");
	    }
		
		if (inclusion.getInclusion() == null) {
	        throw new IllegalArgumentException("Booking Inclusion's package inclusion is missing.");
	    }
		
		if (inclusion.getQuantity() == null) {
	        throw new IllegalArgumentException("Inclusion quantity is missing.");
	    }

		if (inclusion.getQuantity() <= 0) {
	        throw new IllegalArgumentException("Inclusion quantity must at least be one.");
	    }

		if (inclusion.getQuantity() > inclusion.getBooking().getGroupSize()) {
	        throw new IllegalArgumentException("Inclusion quantity cannot exceed booking group size.");
	    }
		
		if (inclusion.getPriceAtBooking() == null) {
	        throw new IllegalArgumentException("Inclusion price at booking is missing.");
	    }
		
		if (inclusion.getPriceAtBooking() < 0) {
	        throw new IllegalArgumentException("Invalid price at booking.");
	    }
		
		return inclusionRepository.save(inclusion);
	}
	
	public BookingInclusion getInclusion(int id) {
		return inclusionRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Booking Inclusion with ID \"" + id + "\" not found."));
	}
	
	public List<BookingInclusion> getAllInclusions() {
		return inclusionRepository.findAll();
	}
	
	public BookingInclusion updateInclusion(BookingInclusion inclusion) {
		if (inclusion.getBooking() == null && 
				inclusion.getInclusion() == null && 
				inclusion.getQuantity() == null && 
				inclusion.getPriceAtBooking() == null) {
			throw new IllegalArgumentException("No fields provided to update for Booking Inclusion.");
		}
		
		BookingInclusion existingInclusion = inclusionRepository.findById(inclusion.getBookingInclusionId())
	            .orElseThrow(() -> new EntityNotFoundException("Booking Inclusion with ID \"" + inclusion.getBookingInclusionId() + "\" not found."));
		

		if (inclusion.getBooking() != null) {
			existingInclusion.setBooking(inclusion.getBooking());
	    }
		
		if (inclusion.getInclusion() != null) {
			existingInclusion.setInclusion(inclusion.getInclusion());
	    }
		
		if (inclusion.getQuantity() != null) {
			if (inclusion.getQuantity() <= 0) {
		        throw new IllegalArgumentException("Inclusion quantity must at least be one.");
		    }

			Booking booking = inclusion.getBooking() != null 
				    ? inclusion.getBooking()
				    : existingInclusion.getBooking();
			
			if (inclusion.getQuantity() > booking.getGroupSize()) {
		        throw new IllegalArgumentException("Inclusion quantity cannot exceed booking group size.");
		    }
			
			existingInclusion.setQuantity(inclusion.getQuantity());
	    }
		
		if (inclusion.getPriceAtBooking() != null) {
			if (inclusion.getPriceAtBooking() < 0) {
		        throw new IllegalArgumentException("Invalid price at booking.");
		    }
			
			existingInclusion.setPriceAtBooking(inclusion.getPriceAtBooking());
	    }
		
		return inclusionRepository.save(existingInclusion);
	}
	
	public void deleteInclusion(int id) {
		BookingInclusion inclusion = inclusionRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Booking Inclusion with ID \"" + id + "\" not found."));
	    
		inclusionRepository.delete(inclusion);
	}
}
