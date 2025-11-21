package com.example.geco.controllers;

import java.time.LocalDate;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.geco.AbstractControllerTest;
import com.example.geco.DataUtil;
import com.example.geco.domains.Booking;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingControllerTests extends AbstractControllerTest{
	@Nested
    class SuccessTests {
		@Test
		public void canAddBooking() throws Exception {
			// Save an account to the database.
			Booking booking = DataUtil.createBookingA(accountService, 
					tourPackageService, 
					packageInclusionService);
			String bookingJson = objectMapper.writeValueAsString(booking);

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
			Booking booking = DataUtil.createBookingA(accountService, 
					tourPackageService, 
					packageInclusionService);
		    Booking savedBookingA = bookingService.addBooking(booking);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.get("/booking/" + savedBookingA.getBookingId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.account.accountId").value(savedBookingA.getAccount().getAccountId())
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
			Booking bookingA = DataUtil.createBookingA(accountService, 
					tourPackageService, 
					packageInclusionService);
			
		    Booking bookingB = DataUtil.createBookingB(accountService, 
					tourPackageService, 
					packageInclusionService);
		    
		    Booking savedBookingA = bookingService.addBooking(bookingA);
		    Booking savedBookingB = bookingService.addBooking(bookingB);
		    
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.get("/booking")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].account.accountId").value(savedBookingA.getAccount().getAccountId())
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
					MockMvcResultMatchers.jsonPath("$[1].account.accountId").value(savedBookingB.getAccount().getAccountId())
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
			Booking bookingA = DataUtil.createBookingA(accountService, 
					tourPackageService, 
					packageInclusionService);
			
		    Booking bookingB = DataUtil.createBookingB(accountService, 
					tourPackageService, 
					packageInclusionService);
			
		    Booking savedBookingA = bookingService.addBooking(bookingA);
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
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].bookingId").value(savedBookingB.getBookingId())
			);
		}
		
		@Test
		public void canUpdateBooking() throws Exception {
			Booking bookingA = DataUtil.createBookingA(accountService, 
					tourPackageService, 
					packageInclusionService);
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
					MockMvcResultMatchers.jsonPath("$.account.accountId").value(savedBookingA.getAccount().getAccountId())
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
		@WithMockUser(roles = "ADMIN")
		public void canDeleteBooking() throws Exception {
			Booking bookingA = DataUtil.createBookingA(accountService, 
					tourPackageService, 
					packageInclusionService);
			
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
			Booking bookingA = DataUtil.createBookingA(accountService, 
					tourPackageService, 
					packageInclusionService);

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
			Booking bookingA = DataUtil.createBookingA(accountService, 
					tourPackageService, 
					packageInclusionService);

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
			Booking bookingA = DataUtil.createBookingA(accountService, 
					tourPackageService, 
					packageInclusionService);

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
			Booking bookingA = DataUtil.createBookingA(accountService, 
					tourPackageService, 
					packageInclusionService);

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
			Booking bookingA = DataUtil.createBookingA(accountService, 
					tourPackageService, 
					packageInclusionService);

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
			Booking bookingA = DataUtil.createBookingA(accountService, 
					tourPackageService, 
					packageInclusionService);
			
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
			Booking bookingA = DataUtil.createBookingA(accountService, 
					tourPackageService, 
					packageInclusionService);
			
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
		@WithMockUser(roles = "ADMIN")
		public void cannotDeleteBooking() throws Exception {
			int id = 0;
		    mockMvc.perform(
					MockMvcRequestBuilders.delete("/booking/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			);
		}
		
		@Test
		public void cannotDeleteBookingNotAdmin() throws Exception {
			Booking bookingA = DataUtil.createBookingA(accountService, 
					tourPackageService, 
					packageInclusionService);
			
		    Booking savedBookingA = bookingService.addBooking(bookingA);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.delete("/booking/" + savedBookingA.getBookingId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isUnauthorized()
			);
		}
	}
}
