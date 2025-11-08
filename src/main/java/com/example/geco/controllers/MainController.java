package com.example.geco.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.Account;
import com.example.geco.domains.Attraction;
import com.example.geco.domains.Booking;
import com.example.geco.domains.Faq;
import com.example.geco.domains.Feedback;
import com.example.geco.domains.PackageInclusion;
import com.example.geco.domains.TourPackage;
import com.example.geco.dto.AccountResponse;
import com.example.geco.dto.SignupRequest;
import com.example.geco.services.AccountService;
import com.example.geco.services.AttractionService;
import com.example.geco.services.BookingService;
import com.example.geco.services.FeedbackService;

@RestController
public class MainController {
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private AttractionService attractionService;
	
	@Autowired
	private BookingService bookingService;
	
	@Autowired
	private FeedbackService feedbackService;
	
	// Functionalities in home page
	@GetMapping
	public HashMap<String, Double> home() {
		HashMap<String, Double> stats = new HashMap<>();
		Double attractions = attractionService.getAttractionsNumber();
		Double monthlyVisitors = bookingService.getAverageVisitor("Month");
		Double avgRating = feedbackService.getAverageRating();
		
		stats.put("attractionsNumber", attractions);
		stats.put("monthlyVisitors", monthlyVisitors);
		stats.put("averageRating", avgRating);
		
        return stats;
    }
	
	// functionalities of admin-dashboard bookings
	@GetMapping("/dashboard/bookings")
	public void displayDashboardBookings() {
		
	}

	// functionalities of admin-dashboard financial
	@GetMapping("/dashboard/finances")
	public void displayDashboardFinances() {
		
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
	
	// to implement
	@PostMapping("/login")
	public void login(@RequestBody Account account) {
	}
	
	// to implement
	@PostMapping("/logout")
	public void logout(@RequestBody Account account) {
		
	}

	@PostMapping("/account")
	public ResponseEntity<?> addAccount(@RequestBody SignupRequest request) {
		try {
			AccountResponse savedAccount  = accountService.addAccount(request);
			return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
			
		} catch (IllegalArgumentException e) {
			Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", e.getMessage());
	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	    }
	}
	
	@PatchMapping("/account")
	public ResponseEntity<?> updateAccount(@RequestBody SignupRequest request) {
		try {
			AccountResponse savedAccount  = accountService.updateAccount(request);
			return new ResponseEntity<>(savedAccount, HttpStatus.OK);
			
		} catch (IllegalArgumentException e) {
			Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", e.getMessage());
	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	    }
	}
	
	
	@PostMapping("/attraction")
	public ResponseEntity<?> addAttraction(@RequestBody Attraction attraction) {
		try {
	        Attraction savedAttraction = attractionService.addAttraction(attraction);
	        return new ResponseEntity<>(savedAttraction, HttpStatus.CREATED);
	        
	    } catch (IllegalArgumentException e) {
			Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", e.getMessage());
	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	    }
	}

	@GetMapping("/attraction/{id}")
	public ResponseEntity<Attraction> getAttraction(@PathVariable int id) {
		Attraction savedAttraction = attractionService.getAttraction(id);
		if (savedAttraction == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(savedAttraction, HttpStatus.OK);
	}

	@GetMapping("/attraction")
	public ResponseEntity<List<Attraction>> getAllAttractions() {
		List<Attraction> savedAttractions = attractionService.getAllAttractions();
		return new ResponseEntity<>(savedAttractions, HttpStatus.OK);
	}
	
	// to implement
	@PutMapping("/attraction/{id}")
	public ResponseEntity<Attraction> updateAttraction(@RequestBody Attraction attraction) {
		return new ResponseEntity<>(new Attraction(), HttpStatus.OK);
	}

	// to implement
	@DeleteMapping("/attraction/{id}")
	public ResponseEntity<Attraction> deleteAttraction(@PathVariable int id) {
		return new ResponseEntity<>(new Attraction(), HttpStatus.OK);
	}
	
	// to implement
	@PostMapping("/feedback")
	public ResponseEntity<?> addFeedback(@RequestBody Feedback feedback) {
		return new ResponseEntity<>(new Feedback(), HttpStatus.CREATED);
	}
	
	// to implement
	@GetMapping("/feedback/{id}")
	public ResponseEntity<Feedback> getFeedback(@PathVariable int id) {
		return new ResponseEntity<>(new Feedback(), HttpStatus.OK);
	}
	
	// to implement
	@GetMapping("/feedback/{category}/{year}/{month}")
	public ResponseEntity<List<Feedback>> getFeedbackByCategory(@PathVariable String category, @PathVariable int year, @PathVariable int month) {
		List<Feedback> feedbacks = new ArrayList();
		return new ResponseEntity<>(feedbacks, HttpStatus.OK);
	}
	
	// to implement
	@PutMapping("/feedback")
	public ResponseEntity<Feedback> updateFeedback(@RequestBody Feedback feedback) {
		return new ResponseEntity<>(new Feedback(), HttpStatus.OK);
	}

	// to implement
	@DeleteMapping("/feedback/{id}")
	public ResponseEntity<Feedback> deleteFeedback(@PathVariable int id) {
		return new ResponseEntity<>(new Feedback(), HttpStatus.OK);
	}
	
	// to implement
	@PostMapping("/faq")
	public ResponseEntity<?> addFaq(@RequestBody Faq faq) {
		return new ResponseEntity<>(new Faq(), HttpStatus.OK);
	}
	
	// to implement
	@GetMapping("/faq/{id}")
	public ResponseEntity<Faq> getFaq(@PathVariable int id) {
		return new ResponseEntity<>(new Faq(), HttpStatus.OK);
	}
	
	// to implement
	@GetMapping("/faq")
	public ResponseEntity<List<Faq>> getAllFaqs() {
		List<Faq> faqs = new ArrayList();
		return new ResponseEntity<>(faqs, HttpStatus.OK);
	}
	
	// to implement
	@PutMapping("/faq")
	public ResponseEntity<Faq> updateFaq(@RequestBody Faq faq) {
		return new ResponseEntity<>(new Faq(), HttpStatus.OK);
	}

	// to implement
	@DeleteMapping("/faq/{id}")
	public ResponseEntity<Faq> deleteFaq(@PathVariable int id) {
		return new ResponseEntity<>(new Faq(), HttpStatus.OK);
	}
	
	// to implement
	@PostMapping("/package")
	public ResponseEntity<?> addPackage(@RequestBody TourPackage tourPackage) {
		return new ResponseEntity<>(new TourPackage(), HttpStatus.OK);
	}
	
	// to implement
	@GetMapping("/package/{id}")
	public ResponseEntity<TourPackage> getPackage(@PathVariable int id) {
		return new ResponseEntity<>(new TourPackage(), HttpStatus.OK);
	}
	
	// to implement
	@GetMapping("/package")
	public ResponseEntity<List<TourPackage>> getAllPackages() {
		List<TourPackage> packages = new ArrayList();
		return new ResponseEntity<>(packages, HttpStatus.OK);
	}
	
	// to implement
	@PutMapping("/package")
	public ResponseEntity<TourPackage> updatePackage(@RequestBody TourPackage tourPackage) {
		return new ResponseEntity<>(new TourPackage(), HttpStatus.OK);
	}

	// to implement
	@DeleteMapping("/package/{id}")
	public ResponseEntity<TourPackage> deletePackage(@PathVariable int id) {
		return new ResponseEntity<>(new TourPackage(), HttpStatus.OK);
	}
	
	// to implement
	@PostMapping("/package-inclusion")
	public ResponseEntity<?> addPackageInclusion(@RequestBody PackageInclusion inclusion) {
		return new ResponseEntity<>(new PackageInclusion(), HttpStatus.OK);
	}
	
	// to implement
	@GetMapping("/package-inclusion/{id}")
	public ResponseEntity<PackageInclusion> getPackageInclusion(@PathVariable int id) {
		return new ResponseEntity<>(new PackageInclusion(), HttpStatus.OK);
	}
	
	// to implement
	@GetMapping("/package-inclusion/not/{id}")
	public ResponseEntity<List<PackageInclusion>> getPackageInclusionNotInPackage(@PathVariable int id) {
		List<PackageInclusion> inclusions = new ArrayList();
		return new ResponseEntity<>(inclusions, HttpStatus.OK);
	}
	
	// to implement
	@GetMapping("/package-inclusion")
	public ResponseEntity<List<PackageInclusion>> getAllPackageInclusions() {
		List<PackageInclusion> inclusions = new ArrayList();
		return new ResponseEntity<>(inclusions, HttpStatus.OK);
	}
	
	// to implement
	@PutMapping("/package-inclusion")
	public ResponseEntity<PackageInclusion> updatePackageInclusion(@RequestBody PackageInclusion faq) {
		return new ResponseEntity<>(new PackageInclusion(), HttpStatus.OK);
	}

	// to implement
	@DeleteMapping("/package-inclusion/{id}")
	public ResponseEntity<PackageInclusion> deletePackageInclusion(@PathVariable int id) {
		return new ResponseEntity<>(new PackageInclusion(), HttpStatus.OK);
	}

	// to implement
	@PostMapping("/booking")
	public ResponseEntity<?> addBooking(@RequestBody Booking booking) {
		return new ResponseEntity<>(new Booking(), HttpStatus.CREATED);
	}

	// to implement
	@GetMapping("/{id}/booking")
	public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable("id") String userId) {
		List<Booking> bookings = new ArrayList();
		return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
	
	// to implement
	@PutMapping("/booking/{id}")
	public ResponseEntity<Booking> updateBooking(@RequestBody Booking booking) {
		return new ResponseEntity<>(new Booking(), HttpStatus.OK);
	}
	
	// to implement
	// payment function, either through gcash api or saving a screenshot of proof of payment
	
	// to implement
	@GetMapping("/calendar/{year}/{month}")
	public ResponseEntity<?> displayCalendar(@PathVariable int year, @PathVariable int month) {
		return new ResponseEntity<>("calendar", HttpStatus.OK);
	}
}
