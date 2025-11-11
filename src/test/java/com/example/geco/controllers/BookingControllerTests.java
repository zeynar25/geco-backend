package com.example.geco.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.geco.DataUtil;
import com.example.geco.domains.Account;
import com.example.geco.domains.Booking;
import com.example.geco.domains.BookingInclusion;
import com.example.geco.domains.PackageInclusion;
import com.example.geco.domains.TourPackage;
import com.example.geco.domains.UserDetail;
import com.example.geco.dto.AccountResponse;
import com.example.geco.dto.SignupRequest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingControllerTests extends AbstractControllerTest{
	@Nested
    class SuccessTests {
		@Test
		public void canAddBooking() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedResponse = accountService.addAccount(request);
			accountA.setAccountId(savedResponse.getAccountId());
			
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusions = new ArrayList<>();
			inclusions.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusions);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking.
			Booking bookingA = new Booking();
			bookingA.setAccount(accountA);
			bookingA.setTourPackage(savedPackageA);
			bookingA.setVisitDate(LocalDate.now().plusDays(3));
			bookingA.setVisitTime(LocalTime.of(10, 0));
			bookingA.setGroupSize(2);
			bookingA.setStatus(Booking.BookingStatus.PENDING);
			bookingA.setTotalPrice(savedPackageA.getBasePrice() * 2);

			// Create BookingInclusion and link to bookingA.
			BookingInclusion bookingInclusionA = new BookingInclusion();
			bookingInclusionA.setBooking(bookingA);       
			bookingInclusionA.setInclusion(inclusionA);
			bookingInclusionA.setQuantity(2);
			bookingInclusionA.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());

			// Set inclusions in booking.
			bookingA.setInclusions(List.of(bookingInclusionA));

			String bookingJson = objectMapper.writeValueAsString(bookingA);

			mockMvc.perform(
			        MockMvcRequestBuilders.post("/booking")
			            .contentType(MediaType.APPLICATION_JSON)
			            .content(bookingJson)
			).andExpect(
					MockMvcResultMatchers.status().isCreated()
			);
		}
		
		@Test
		public void canGetBooking() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedResponse = accountService.addAccount(request);
			accountA.setAccountId(savedResponse.getAccountId());
			
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusionsA = new ArrayList<>();
			inclusionsA.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusionsA);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking.
			Booking bookingA = new Booking();
			bookingA.setAccount(accountA);
			bookingA.setTourPackage(savedPackageA);
			bookingA.setVisitDate(LocalDate.now().plusDays(3));
			bookingA.setVisitTime(LocalTime.of(10, 0));
			bookingA.setGroupSize(2);
			bookingA.setStatus(Booking.BookingStatus.PENDING);
			bookingA.setTotalPrice(savedPackageA.getBasePrice() * 2);

			// Create BookingInclusion and link to bookingA.
			BookingInclusion bookingInclusionA = new BookingInclusion();
			bookingInclusionA.setBooking(bookingA);       
			bookingInclusionA.setInclusion(inclusionA);
			bookingInclusionA.setQuantity(2);
			bookingInclusionA.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());

			// Set inclusions in booking.
			bookingA.setInclusions(List.of(bookingInclusionA));
		    
		    Booking savedBookingA = bookingService.addBooking(bookingA);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.get("/booking/" + savedBookingA.getBookingId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.account").value(savedBookingA.getAccount())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.tourPackage.packageId").value(savedBookingA.getTourPackage().getPackageId())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.inclusions").exists()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.visitDate").value(savedBookingA.getVisitDate().toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.visitTime").value(savedBookingA.getVisitTime().toString() + ":00")
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.groupSize").value(savedBookingA.getGroupSize())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.status").value(savedBookingA.getStatus().name())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.totalPrice").value(savedBookingA.getTotalPrice())
			);
		}
		
		@Test
		public void canGetAllBookings() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedResponse = accountService.addAccount(request);
			accountA.setAccountId(savedResponse.getAccountId());
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusions = new ArrayList<>();
			inclusions.add(inclusionA);

			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusions);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			
			
			// Create Booking A.
			Booking bookingA = new Booking();
			bookingA.setAccount(accountA);
			bookingA.setTourPackage(savedPackageA);
			bookingA.setVisitDate(LocalDate.now().plusDays(3));
			bookingA.setVisitTime(LocalTime.of(10, 0));
			bookingA.setGroupSize(2);
			bookingA.setStatus(Booking.BookingStatus.PENDING);
			bookingA.setTotalPrice(savedPackageA.getBasePrice() * bookingA.getGroupSize());

			// Create BookingInclusion and link to bookingA.
			BookingInclusion bookingInclusionA = new BookingInclusion();
			bookingInclusionA.setBooking(bookingA);       
			bookingInclusionA.setInclusion(inclusionA);
			bookingInclusionA.setQuantity(2);
			bookingInclusionA.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());
		
			// Set inclusions in booking.
			bookingA.setInclusions(List.of(bookingInclusionA));
		    Booking savedBookingA = bookingService.addBooking(bookingA);
			
		    
		    
		    // Create Booking B
		    
			Booking bookingB = new Booking();
			bookingB.setAccount(accountA);
			bookingB.setTourPackage(savedPackageA);
			bookingB.setVisitDate(LocalDate.now().plusDays(5));
			bookingB.setVisitTime(LocalTime.of(10, 0));
			bookingB.setGroupSize(2);
			bookingB.setStatus(Booking.BookingStatus.PENDING);
			bookingB.setTotalPrice(savedPackageA.getBasePrice() * bookingB.getGroupSize());
			
			BookingInclusion bookingInclusionB = new BookingInclusion();
			bookingInclusionB.setBooking(bookingB);       
			bookingInclusionB.setInclusion(inclusionA);
			bookingInclusionB.setQuantity(1);
			bookingInclusionB.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());

			bookingB.setInclusions(List.of(bookingInclusionB));
		    Booking savedBookingB = bookingService.addBooking(bookingB);
		    
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.get("/booking")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].account").value(savedBookingA.getAccount())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].tourPackage.packageId").value(savedBookingA.getTourPackage().getPackageId())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].inclusions").exists()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].visitDate").value(savedBookingA.getVisitDate().toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].visitTime").value(savedBookingA.getVisitTime().toString() + ":00")
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].groupSize").value(savedBookingA.getGroupSize())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].status").value(savedBookingA.getStatus().name())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].totalPrice").value(savedBookingA.getTotalPrice())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].account").value(savedBookingB.getAccount())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].tourPackage.packageId").value(savedBookingB.getTourPackage().getPackageId())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].inclusions").exists()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].visitDate").value(savedBookingB.getVisitDate().toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].visitTime").value(savedBookingB.getVisitTime().toString() + ":00")
    		).andExpect(
    				MockMvcResultMatchers.jsonPath("$[1].groupSize").value(savedBookingB.getGroupSize())
    		).andExpect(
    				MockMvcResultMatchers.jsonPath("$[1].status").value(savedBookingB.getStatus().name())
    		).andExpect(
    				MockMvcResultMatchers.jsonPath("$[1].totalPrice").value(savedBookingB.getTotalPrice())
    		);
		}
		
		@Test
		public void canGetAllBookingsWithinFewDays() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedResponse = accountService.addAccount(request);
			accountA.setAccountId(savedResponse.getAccountId());
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusions = new ArrayList<>();
			inclusions.add(inclusionA);

			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusions);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			
			
			// Create Booking A.
			Booking bookingA = new Booking();
			bookingA.setAccount(accountA);
			bookingA.setTourPackage(savedPackageA);
			bookingA.setVisitDate(LocalDate.now().plusDays(3));
			bookingA.setVisitTime(LocalTime.of(10, 0));
			bookingA.setGroupSize(2);
			bookingA.setStatus(Booking.BookingStatus.PENDING);
			bookingA.setTotalPrice(savedPackageA.getBasePrice() * bookingA.getGroupSize());

			// Create BookingInclusion and link to bookingA.
			BookingInclusion bookingInclusionA = new BookingInclusion();
			bookingInclusionA.setBooking(bookingA);       
			bookingInclusionA.setInclusion(inclusionA);
			bookingInclusionA.setQuantity(2);
			bookingInclusionA.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());
		
			// Set inclusions in booking.
			bookingA.setInclusions(List.of(bookingInclusionA));
		    Booking savedBookingA = bookingService.addBooking(bookingA);
			
		    
		    
		    // Create Booking B
		    
			Booking bookingB = new Booking();
			bookingB.setAccount(accountA);
			bookingB.setTourPackage(savedPackageA);
			bookingB.setVisitDate(LocalDate.now().plusDays(5));
			bookingB.setVisitTime(LocalTime.of(10, 0));
			bookingB.setGroupSize(2);
			bookingB.setStatus(Booking.BookingStatus.PENDING);
			bookingB.setTotalPrice(savedPackageA.getBasePrice() * bookingB.getGroupSize());
			
			BookingInclusion bookingInclusionB = new BookingInclusion();
			bookingInclusionB.setBooking(bookingB);       
			bookingInclusionB.setInclusion(inclusionA);
			bookingInclusionB.setQuantity(1);
			bookingInclusionB.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());

			bookingB.setInclusions(List.of(bookingInclusionB));
		    Booking savedBookingB = bookingService.addBooking(bookingB);
		    
		    String startDate = LocalDate.now().toString();
		    String endDate = LocalDate.now().plusDays(4).toString(); 
		    
		 // Should expects one booking within 4 days.
		    mockMvc.perform(
					MockMvcRequestBuilders.get("/booking")
						.param("startDate", startDate)	
						.param("endDate", endDate)	
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.length()").value(1)
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].bookingId").value(savedBookingA.getBookingId())
			);
		    
		    // Should expects two bookings within 5 days.
		    endDate = LocalDate.now().plusDays(5).toString(); 
		    mockMvc.perform(
					MockMvcRequestBuilders.get("/booking")
						.param("startDate", startDate)	
						.param("endDate", endDate)	
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.length()").value(2)
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].bookingId").value(savedBookingA.getBookingId())
			);
		}
		
		@Test
		public void canUpdateBooking() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedResponse = accountService.addAccount(request);
			accountA.setAccountId(savedResponse.getAccountId());
			
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusionsA = new ArrayList<>();
			inclusionsA.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusionsA);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking.
			Booking bookingA = new Booking();
			bookingA.setAccount(accountA);
			bookingA.setTourPackage(savedPackageA);
			bookingA.setVisitDate(LocalDate.now().plusDays(3));
			bookingA.setVisitTime(LocalTime.of(10, 0));
			bookingA.setGroupSize(2);
			bookingA.setStatus(Booking.BookingStatus.PENDING);
			bookingA.setTotalPrice(savedPackageA.getBasePrice() * 2);

			// Create BookingInclusion and link to bookingA.
			BookingInclusion bookingInclusionA = new BookingInclusion();
			bookingInclusionA.setBooking(bookingA);       
			bookingInclusionA.setInclusion(inclusionA);
			bookingInclusionA.setQuantity(2);
			bookingInclusionA.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());

			// Set inclusions in booking.
			bookingA.setInclusions(List.of(bookingInclusionA));
		    Booking savedBookingA = bookingService.addBooking(bookingA);
		   
		    Booking newBooking = new Booking();
		    newBooking.setBookingId(savedBookingA.getBookingId());
		    newBooking.setStatus(Booking.BookingStatus.ACCEPTED);
		    
			String bookingJson = objectMapper.writeValueAsString(newBooking);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.patch("/booking/" + savedBookingA.getBookingId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(bookingJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.account").value(savedBookingA.getAccount())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.tourPackage.packageId").value(savedBookingA.getTourPackage().getPackageId())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.inclusions").exists()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.visitDate").value(savedBookingA.getVisitDate().toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.visitTime").value(savedBookingA.getVisitTime().toString() + ":00")
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.groupSize").value(savedBookingA.getGroupSize())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.status").value(newBooking.getStatus().name())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.totalPrice").value(savedBookingA.getTotalPrice())
			);
		}
		
		@Test
		public void canDeleteBooking() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedResponse = accountService.addAccount(request);
			accountA.setAccountId(savedResponse.getAccountId());
			
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusions = new ArrayList<>();
			inclusions.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusions);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking.
			Booking bookingA = new Booking();
			bookingA.setAccount(accountA);
			bookingA.setTourPackage(savedPackageA);
			bookingA.setVisitDate(LocalDate.now().plusDays(3));
			bookingA.setVisitTime(LocalTime.of(10, 0));
			bookingA.setGroupSize(2);
			bookingA.setStatus(Booking.BookingStatus.PENDING);
			bookingA.setTotalPrice(savedPackageA.getBasePrice() * 2);

			// Create BookingInclusion and link to bookingA.
			BookingInclusion bookingInclusionA = new BookingInclusion();
			bookingInclusionA.setBooking(bookingA);       
			bookingInclusionA.setInclusion(inclusionA);
			bookingInclusionA.setQuantity(2);
			bookingInclusionA.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());

			// Set inclusions in booking.
			bookingA.setInclusions(List.of(bookingInclusionA));
		    Booking savedBookingA = bookingService.addBooking(bookingA);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.delete("/booking/" + savedBookingA.getBookingId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNoContent()
			);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.get("/booking/" + savedBookingA.getBookingId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			);
		}
	}
	
	@Nested
    class FailureTests {
		@Test
		public void cannotAddBookingMissingAccount() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedResponse = accountService.addAccount(request);
			accountA.setAccountId(savedResponse.getAccountId());
			
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusions = new ArrayList<>();
			inclusions.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusions);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking.
			Booking bookingA = new Booking();
			bookingA.setAccount(accountA);
			bookingA.setTourPackage(savedPackageA);
			bookingA.setVisitDate(LocalDate.now().plusDays(3));
			bookingA.setVisitTime(LocalTime.of(10, 0));
			bookingA.setGroupSize(2);
			bookingA.setStatus(Booking.BookingStatus.PENDING);
			bookingA.setTotalPrice(savedPackageA.getBasePrice() * 2);

			// Create BookingInclusion and link to bookingA.
			BookingInclusion bookingInclusionA = new BookingInclusion();
			bookingInclusionA.setBooking(bookingA);       
			bookingInclusionA.setInclusion(inclusionA);
			bookingInclusionA.setQuantity(2);
			bookingInclusionA.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());

			// Set inclusions in booking.
			bookingA.setInclusions(List.of(bookingInclusionA));

			bookingA.setAccount(null);
			String bookingJson = objectMapper.writeValueAsString(bookingA);

			mockMvc.perform(
			        MockMvcRequestBuilders.post("/booking")
			            .contentType(MediaType.APPLICATION_JSON)
			            .content(bookingJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Booking's account is missing.")
			);
		}
		
		@Test
		public void cannotAddBookingMissingPackage() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedResponse = accountService.addAccount(request);
			accountA.setAccountId(savedResponse.getAccountId());
			
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusions = new ArrayList<>();
			inclusions.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusions);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking.
			Booking bookingA = new Booking();
			bookingA.setAccount(accountA);
			bookingA.setTourPackage(savedPackageA);
			bookingA.setVisitDate(LocalDate.now().plusDays(3));
			bookingA.setVisitTime(LocalTime.of(10, 0));
			bookingA.setGroupSize(2);
			bookingA.setStatus(Booking.BookingStatus.PENDING);
			bookingA.setTotalPrice(savedPackageA.getBasePrice() * 2);

			// Create BookingInclusion and link to bookingA.
			BookingInclusion bookingInclusionA = new BookingInclusion();
			bookingInclusionA.setBooking(bookingA);       
			bookingInclusionA.setInclusion(inclusionA);
			bookingInclusionA.setQuantity(2);
			bookingInclusionA.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());

			// Set inclusions in booking.
			bookingA.setInclusions(List.of(bookingInclusionA));

			bookingA.setTourPackage(null);
			String bookingJson = objectMapper.writeValueAsString(bookingA);

			mockMvc.perform(
			        MockMvcRequestBuilders.post("/booking")
			            .contentType(MediaType.APPLICATION_JSON)
			            .content(bookingJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Booking's Tour Package is missing.")
			);
		}
		
		@Test
		public void cannotAddBookingMissingVisitDate() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedResponse = accountService.addAccount(request);
			accountA.setAccountId(savedResponse.getAccountId());
			
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusions = new ArrayList<>();
			inclusions.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusions);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking.
			Booking bookingA = new Booking();
			bookingA.setAccount(accountA);
			bookingA.setTourPackage(savedPackageA);
			bookingA.setVisitDate(LocalDate.now().plusDays(3));
			bookingA.setVisitTime(LocalTime.of(10, 0));
			bookingA.setGroupSize(2);
			bookingA.setStatus(Booking.BookingStatus.PENDING);
			bookingA.setTotalPrice(savedPackageA.getBasePrice() * 2);

			// Create BookingInclusion and link to bookingA.
			BookingInclusion bookingInclusionA = new BookingInclusion();
			bookingInclusionA.setBooking(bookingA);       
			bookingInclusionA.setInclusion(inclusionA);
			bookingInclusionA.setQuantity(2);
			bookingInclusionA.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());

			// Set inclusions in booking.
			bookingA.setInclusions(List.of(bookingInclusionA));

			bookingA.setVisitDate(null);
			String bookingJson = objectMapper.writeValueAsString(bookingA);

			mockMvc.perform(
			        MockMvcRequestBuilders.post("/booking")
			            .contentType(MediaType.APPLICATION_JSON)
			            .content(bookingJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Booking's visit date is missing.")
			);
		}
		
		@Test
		public void cannotAddBookingMissingVisitTime() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedResponse = accountService.addAccount(request);
			accountA.setAccountId(savedResponse.getAccountId());
			
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusions = new ArrayList<>();
			inclusions.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusions);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking.
			Booking bookingA = new Booking();
			bookingA.setAccount(accountA);
			bookingA.setTourPackage(savedPackageA);
			bookingA.setVisitDate(LocalDate.now().plusDays(3));
			bookingA.setVisitTime(LocalTime.of(10, 0));
			bookingA.setGroupSize(2);
			bookingA.setStatus(Booking.BookingStatus.PENDING);
			bookingA.setTotalPrice(savedPackageA.getBasePrice() * 2);

			// Create BookingInclusion and link to bookingA.
			BookingInclusion bookingInclusionA = new BookingInclusion();
			bookingInclusionA.setBooking(bookingA);       
			bookingInclusionA.setInclusion(inclusionA);
			bookingInclusionA.setQuantity(2);
			bookingInclusionA.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());

			// Set inclusions in booking.
			bookingA.setInclusions(List.of(bookingInclusionA));

			bookingA.setVisitTime(null);
			String bookingJson = objectMapper.writeValueAsString(bookingA);

			mockMvc.perform(
			        MockMvcRequestBuilders.post("/booking")
			            .contentType(MediaType.APPLICATION_JSON)
			            .content(bookingJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Booking's visit time is missing.")
			);
		}
		
		@Test
		public void cannotAddBookingMissingGroupSize() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedResponse = accountService.addAccount(request);
			accountA.setAccountId(savedResponse.getAccountId());
			
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusions = new ArrayList<>();
			inclusions.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusions);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking.
			Booking bookingA = new Booking();
			bookingA.setAccount(accountA);
			bookingA.setTourPackage(savedPackageA);
			bookingA.setVisitDate(LocalDate.now().plusDays(3));
			bookingA.setVisitTime(LocalTime.of(10, 0));
			bookingA.setGroupSize(2);
			bookingA.setStatus(Booking.BookingStatus.PENDING);
			bookingA.setTotalPrice(savedPackageA.getBasePrice() * 2);

			// Create BookingInclusion and link to bookingA.
			BookingInclusion bookingInclusionA = new BookingInclusion();
			bookingInclusionA.setBooking(bookingA);       
			bookingInclusionA.setInclusion(inclusionA);
			bookingInclusionA.setQuantity(2);
			bookingInclusionA.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());

			// Set inclusions in booking.
			bookingA.setInclusions(List.of(bookingInclusionA));

			bookingA.setGroupSize(null);
			String bookingJson = objectMapper.writeValueAsString(bookingA);

			mockMvc.perform(
			        MockMvcRequestBuilders.post("/booking")
			            .contentType(MediaType.APPLICATION_JSON)
			            .content(bookingJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Booking's group size is missing.")
			);
		}
		
		@Test
		public void cannotAddBookingInvalidGroupSize() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedResponse = accountService.addAccount(request);
			accountA.setAccountId(savedResponse.getAccountId());
			
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusions = new ArrayList<>();
			inclusions.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusions);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking.
			Booking bookingA = new Booking();
			bookingA.setAccount(accountA);
			bookingA.setTourPackage(savedPackageA);
			bookingA.setVisitDate(LocalDate.now().plusDays(3));
			bookingA.setVisitTime(LocalTime.of(10, 0));
			bookingA.setGroupSize(2);
			bookingA.setStatus(Booking.BookingStatus.PENDING);
			bookingA.setTotalPrice(savedPackageA.getBasePrice() * 2);

			// Create BookingInclusion and link to bookingA.
			BookingInclusion bookingInclusionA = new BookingInclusion();
			bookingInclusionA.setBooking(bookingA);       
			bookingInclusionA.setInclusion(inclusionA);
			bookingInclusionA.setQuantity(2);
			bookingInclusionA.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());

			// Set inclusions in booking.
			bookingA.setInclusions(List.of(bookingInclusionA));

			bookingA.setGroupSize(-1);
			String bookingJson = objectMapper.writeValueAsString(bookingA);

			mockMvc.perform(
			        MockMvcRequestBuilders.post("/booking")
			            .contentType(MediaType.APPLICATION_JSON)
			            .content(bookingJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Invalid Booking's group size.")
			);
		}
		
		@Test
		public void cannotGetBooking() throws Exception {
			int id = 0;
		     
		    mockMvc.perform(
					MockMvcRequestBuilders.get("/booking/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Booking with ID \"" + id + "\" not found.")
			);
		}
		
		@Test
		public void cannotUpdateBooking() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedResponse = accountService.addAccount(request);
			accountA.setAccountId(savedResponse.getAccountId());
			
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusionsA = new ArrayList<>();
			inclusionsA.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusionsA);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking.
			Booking bookingA = new Booking();
			bookingA.setAccount(accountA);
			bookingA.setTourPackage(savedPackageA);
			bookingA.setVisitDate(LocalDate.now().plusDays(3));
			bookingA.setVisitTime(LocalTime.of(10, 0));
			bookingA.setGroupSize(2);
			bookingA.setStatus(Booking.BookingStatus.PENDING);
			bookingA.setTotalPrice(savedPackageA.getBasePrice() * 2);

			// Create BookingInclusion and link to bookingA.
			BookingInclusion bookingInclusionA = new BookingInclusion();
			bookingInclusionA.setBooking(bookingA);       
			bookingInclusionA.setInclusion(inclusionA);
			bookingInclusionA.setQuantity(2);
			bookingInclusionA.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());

			// Set inclusions in booking.
			bookingA.setInclusions(List.of(bookingInclusionA));
		    Booking savedBookingA = bookingService.addBooking(bookingA);
		   
		    Booking newBooking = new Booking();
			String bookingJson = objectMapper.writeValueAsString(newBooking);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.patch("/booking/" + savedBookingA.getBookingId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(bookingJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("No fields provided to update for Booking.")
			);
		}
		
		@Test
		public void cannotDeleteBooking() throws Exception {
			int id = 0;
		    mockMvc.perform(
					MockMvcRequestBuilders.delete("/booking/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			);
		}
	}
}
