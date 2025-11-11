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
import com.example.geco.domains.Feedback;
import com.example.geco.domains.FeedbackCategory;
import com.example.geco.domains.PackageInclusion;
import com.example.geco.domains.TourPackage;
import com.example.geco.domains.UserDetail;
import com.example.geco.dto.AccountResponse;
import com.example.geco.dto.FeedbackResponse;
import com.example.geco.dto.SignupRequest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FeedbackControllerTests extends AbstractControllerTest {
	@Nested
    class SuccessTests {
		@Test
		public void canAddFeedback() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedAccountResponse = accountService.addAccount(request);
			accountA.setAccountId(savedAccountResponse.getAccountId());
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusionsA = new ArrayList<>();
			inclusionsA.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusionsA);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking A.
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
		    
		    FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			FeedbackCategory savedCategoryA = feedbackCategoryService.addCategory(categoryA);
		    
		    Feedback feedbackA = DataUtil.createFeedbackA(accountA ,savedBookingA, savedCategoryA);
		    
			String feedbackJson = objectMapper.writeValueAsString(feedbackA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/feedback")
						.contentType(MediaType.APPLICATION_JSON)
						.content(feedbackJson)
			).andExpect(
					MockMvcResultMatchers.status().isCreated()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackId").exists()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.account.accountId").value(feedbackA.getAccount().getAccountId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.booking.bookingId").value(feedbackA.getBooking().getBookingId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.category").value(feedbackA.getCategory().getLabel())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.stars").value(feedbackA.getStars())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.comment").value(feedbackA.getComment())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.suggestion").value(feedbackA.getSuggestion())
			);
		}
		
		@Test
		public void canGetFeedback() throws Exception {
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedAccountResponse = accountService.addAccount(request);
			accountA.setAccountId(savedAccountResponse.getAccountId());
			
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusionsA = new ArrayList<>();
			inclusionsA.add(inclusionA);
	
			
			TourPackage packageA = DataUtil.createPackageA(inclusionsA);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			Booking bookingA = new Booking();
			bookingA.setAccount(accountA);
			bookingA.setTourPackage(savedPackageA);
			bookingA.setVisitDate(LocalDate.now().plusDays(3));
			bookingA.setVisitTime(LocalTime.of(10, 0));
			bookingA.setGroupSize(2);
			bookingA.setStatus(Booking.BookingStatus.PENDING);
			bookingA.setTotalPrice(savedPackageA.getBasePrice() * 2);
	
			BookingInclusion bookingInclusionA = new BookingInclusion();
			bookingInclusionA.setBooking(bookingA);       
			bookingInclusionA.setInclusion(inclusionA);
			bookingInclusionA.setQuantity(2);
			bookingInclusionA.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());
	
			bookingA.setInclusions(List.of(bookingInclusionA));
		    Booking savedBookingA = bookingService.addBooking(bookingA);
		    
		    FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			FeedbackCategory savedCategoryA = feedbackCategoryService.addCategory(categoryA);
		    
		    Feedback feedbackA = DataUtil.createFeedbackA(accountA ,savedBookingA, savedCategoryA);
		    FeedbackResponse savedFeedbackA = feedbackService.addFeedback(feedbackA);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback/" + savedFeedbackA.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackId").value(savedFeedbackA.getFeedbackId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.account.accountId").value(savedFeedbackA.getAccount().getAccountId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.booking.bookingId").value(savedFeedbackA.getBooking().getBookingId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.category").value(savedFeedbackA.getCategory())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.stars").value(savedFeedbackA.getStars())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.comment").value(savedFeedbackA.getComment())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.suggestion").value(savedFeedbackA.getSuggestion())
			);
		}
		
		@Test
		public void canGetAllFeedbacks() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedAccountResponse = accountService.addAccount(request);
			accountA.setAccountId(savedAccountResponse.getAccountId());
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusionsA = new ArrayList<>();
			inclusionsA.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusionsA);
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
		    
		    FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			FeedbackCategory savedCategoryA = feedbackCategoryService.addCategory(categoryA);
		    
		    Feedback feedbackA = DataUtil.createFeedbackA(accountA ,savedBookingA, savedCategoryA);
		    FeedbackResponse savedFeedbackA = feedbackService.addFeedback(feedbackA);
		    
		    
		    // Create Booking B.
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
 			bookingInclusionB.setQuantity(2);
 			bookingInclusionB.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());

 			bookingA.setInclusions(List.of(bookingInclusionA));
 		    Booking savedBookingB = bookingService.addBooking(bookingB);
 		    
 		    Feedback feedbackB = DataUtil.createFeedbackA(accountA ,savedBookingB, savedCategoryA);
 		    FeedbackResponse savedFeedbackB = feedbackService.addFeedback(feedbackB);
		    
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].feedbackId").value(savedFeedbackA.getFeedbackId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].account.accountId").value(savedFeedbackA.getAccount().getAccountId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].booking.bookingId").value(savedFeedbackA.getBooking().getBookingId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].category").value(savedFeedbackA.getCategory())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].stars").value(savedFeedbackA.getStars())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].comment").value(savedFeedbackA.getComment())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].suggestion").value(savedFeedbackA.getSuggestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].feedbackId").value(savedFeedbackB.getFeedbackId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].account.accountId").value(savedFeedbackB.getAccount().getAccountId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].booking.bookingId").value(savedFeedbackB.getBooking().getBookingId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].category").value(savedFeedbackB.getCategory())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].stars").value(savedFeedbackB.getStars())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].comment").value(savedFeedbackB.getComment())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].suggestion").value(savedFeedbackB.getSuggestion())
			);
		}
		
		@Test
		public void canGetFeedbackByCategory() throws Exception {
			
		}
		
		@Test
		public void canUpdateFeedbackStars() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedAccountResponse = accountService.addAccount(request);
			accountA.setAccountId(savedAccountResponse.getAccountId());
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusionsA = new ArrayList<>();
			inclusionsA.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusionsA);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking A.
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
		    
		    FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			FeedbackCategory savedCategoryA = feedbackCategoryService.addCategory(categoryA);
		    
		    Feedback feedbackA = DataUtil.createFeedbackA(accountA ,savedBookingA, savedCategoryA);
		    FeedbackResponse savedFeedbackA = feedbackService.addFeedback(feedbackA);
		    
		    Feedback newFeedback = new Feedback();
		    newFeedback.setStars(5.0);
		    
			String feedbackJson = objectMapper.writeValueAsString(newFeedback);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/" + savedFeedbackA.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(feedbackJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackId").value(savedFeedbackA.getFeedbackId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.account.accountId").value(savedFeedbackA.getAccount().getAccountId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.booking.bookingId").value(savedFeedbackA.getBooking().getBookingId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.category").value(savedFeedbackA.getCategory())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.stars").value(newFeedback.getStars())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.comment").value(savedFeedbackA.getComment())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.suggestion").value(savedFeedbackA.getSuggestion())
			);
		}
		
		@Test
		public void canDeleteFeedback() throws Exception {
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedAccountResponse = accountService.addAccount(request);
			accountA.setAccountId(savedAccountResponse.getAccountId());
			
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusionsA = new ArrayList<>();
			inclusionsA.add(inclusionA);
	
			
			TourPackage packageA = DataUtil.createPackageA(inclusionsA);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			Booking bookingA = new Booking();
			bookingA.setAccount(accountA);
			bookingA.setTourPackage(savedPackageA);
			bookingA.setVisitDate(LocalDate.now().plusDays(3));
			bookingA.setVisitTime(LocalTime.of(10, 0));
			bookingA.setGroupSize(2);
			bookingA.setStatus(Booking.BookingStatus.PENDING);
			bookingA.setTotalPrice(savedPackageA.getBasePrice() * 2);
	
			BookingInclusion bookingInclusionA = new BookingInclusion();
			bookingInclusionA.setBooking(bookingA);       
			bookingInclusionA.setInclusion(inclusionA);
			bookingInclusionA.setQuantity(2);
			bookingInclusionA.setPriceAtBooking(inclusionA.getInclusionPricePerPerson());
	
			bookingA.setInclusions(List.of(bookingInclusionA));
		    Booking savedBookingA = bookingService.addBooking(bookingA);
		    
		    FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			FeedbackCategory savedCategoryA = feedbackCategoryService.addCategory(categoryA);
		    
		    Feedback feedbackA = DataUtil.createFeedbackA(accountA ,savedBookingA, savedCategoryA);
		    FeedbackResponse savedFeedbackA = feedbackService.addFeedback(feedbackA);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.delete("/feedback/" + savedFeedbackA.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNoContent()
			);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback/" + savedFeedbackA.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Feedback with ID \"" + savedFeedbackA.getFeedbackId() + "\" not found.")
		    );
		}
	}
	
	@Nested
    class FailureTests {
		@Test
		public void cannotAddFeedbackNullAccount() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedAccountResponse = accountService.addAccount(request);
			accountA.setAccountId(savedAccountResponse.getAccountId());
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusionsA = new ArrayList<>();
			inclusionsA.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusionsA);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking A.
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
		    
		    FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			FeedbackCategory savedCategoryA = feedbackCategoryService.addCategory(categoryA);
		    
		    Feedback feedbackA = DataUtil.createFeedbackA(accountA ,savedBookingA, savedCategoryA);
		    feedbackA.setAccount(null);
		    
			String feedbackJson = objectMapper.writeValueAsString(feedbackA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/feedback")
						.contentType(MediaType.APPLICATION_JSON)
						.content(feedbackJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Account is missing or invalid.")
			);
		}
		
		@Test
		public void cannotAddFeedbackAccountNotFound() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedAccountResponse = accountService.addAccount(request);
			accountA.setAccountId(savedAccountResponse.getAccountId());
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusionsA = new ArrayList<>();
			inclusionsA.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusionsA);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking A.
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
		    
		    FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			FeedbackCategory savedCategoryA = feedbackCategoryService.addCategory(categoryA);
		    
		    Feedback feedbackA = DataUtil.createFeedbackA(accountA ,savedBookingA, savedCategoryA);
		    
		    UserDetail detailB = DataUtil.createUserDetailB();
			Account accountB = DataUtil.createAccountA(detailB);
			accountB.setAccountId(666);
		    feedbackA.setAccount(accountB);
		    
			String feedbackJson = objectMapper.writeValueAsString(feedbackA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/feedback")
						.contentType(MediaType.APPLICATION_JSON)
						.content(feedbackJson)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Account not found.")
			);
		}
		
		@Test
		public void cannotGetFeedback() throws Exception {
			int id = 0;
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Feedback with ID \"" + id + "\" not found.")
		    );
		}
	    
	    @Test
		public void cannotUpdateFeedbackMissingFields() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedAccountResponse = accountService.addAccount(request);
			accountA.setAccountId(savedAccountResponse.getAccountId());
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusionsA = new ArrayList<>();
			inclusionsA.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusionsA);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking A.
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
		    
		    FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			FeedbackCategory savedCategoryA = feedbackCategoryService.addCategory(categoryA);
		    
		    Feedback feedbackA = DataUtil.createFeedbackA(accountA ,savedBookingA, savedCategoryA);
		    FeedbackResponse savedFeedbackA = feedbackService.addFeedback(feedbackA);
		    
		    Feedback newFeedback = new Feedback();
			String feedbackJson = objectMapper.writeValueAsString(newFeedback);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/" + savedFeedbackA.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(feedbackJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("No fields provided to update feedback.")
			);
		}
	    
	    @Test
		public void cannotUpdateFeedbackNotFound() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedAccountResponse = accountService.addAccount(request);
			accountA.setAccountId(savedAccountResponse.getAccountId());
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusionsA = new ArrayList<>();
			inclusionsA.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusionsA);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking A.
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
		    
		    FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			FeedbackCategory savedCategoryA = feedbackCategoryService.addCategory(categoryA);
		    
		    Feedback feedbackA = DataUtil.createFeedbackA(accountA ,savedBookingA, savedCategoryA);
		    FeedbackResponse savedFeedbackA = feedbackService.addFeedback(feedbackA);
		    
		    Feedback newFeedback = new Feedback();
		    newFeedback.setStars(0.0);
			String feedbackJson = objectMapper.writeValueAsString(newFeedback);
			
			int id = 69;
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(feedbackJson)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Feedback with ID \"" + id + "\" not found.")
			);
		}
	    
	    @Test
		public void cannotUpdateFeedbackInvalidCategory() throws Exception {
			// Save an account to the database.
			UserDetail detailA = DataUtil.createUserDetailA();
			Account accountA = DataUtil.createAccountA(detailA);
			SignupRequest request = new SignupRequest(accountA, detailA);
			AccountResponse savedAccountResponse = accountService.addAccount(request);
			accountA.setAccountId(savedAccountResponse.getAccountId());
			
			// Save packageInclusion to the database.
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			packageInclusionService.addInclusion(inclusionA);
			List<PackageInclusion> inclusionsA = new ArrayList<>();
			inclusionsA.add(inclusionA);

			
			// Save tourPackage to the database.
			TourPackage packageA = DataUtil.createPackageA(inclusionsA);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			// Create Booking A.
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
		    
		    FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			FeedbackCategory savedCategoryA = feedbackCategoryService.addCategory(categoryA);
		    
		    Feedback feedbackA = DataUtil.createFeedbackA(accountA ,savedBookingA, savedCategoryA);
		    FeedbackResponse savedFeedbackA = feedbackService.addFeedback(feedbackA);
		    
		    Feedback newFeedback = new Feedback();
		    FeedbackCategory newCategory = new FeedbackCategory();
		    newCategory.setFeedbackCategoryId(102);
		    newFeedback.setCategory(newCategory);
		    
			String feedbackJson = objectMapper.writeValueAsString(newFeedback);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/" + savedFeedbackA.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(feedbackJson)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Category not found.")
			);
		}
	    
	    @Test
		public void cannotDeleteFeedback() throws Exception {
			int id = 0;
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.delete("/feedback/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Feedback with ID \"" + id + "\" not found.")
		    );
		}
	}
}
