package com.example.geco.controllers;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.geco.AbstractControllerTest;
import com.example.geco.DataUtil;
import com.example.geco.domains.Account;
import com.example.geco.domains.Feedback;
import com.example.geco.domains.FeedbackCategory;
import com.example.geco.domains.Feedback.FeedbackStatus;
import com.example.geco.dto.FeedbackResponse;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FeedbackControllerTests extends AbstractControllerTest {
	@Nested
    class SuccessTests {
		@Test
		public void canAddFeedback() throws Exception {
		    Feedback feedbackA = DataUtil.createFeedbackA(accountService, 
					tourPackageService, 
					packageInclusionService,
					bookingService,
					feedbackCategoryService);
		    
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
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.status").value(FeedbackStatus.NEW.toString())
			);
		}
		
		@Test
		public void canGetFeedback() throws Exception {
			Feedback feedbackA = DataUtil.createFeedbackA(accountService, 
					tourPackageService, 
					packageInclusionService,
					bookingService,
					feedbackCategoryService);
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
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.status").value(FeedbackStatus.NEW.toString())
			);
		}
		
		// I'm commenting this out because 
		// DataUtil createFeedback's createBooking is 
		// booked into the future but my get all feedback only read feedback 
		// made up to current date. 
		// (this is because you can only at most book two days prior to a date)
//		@Test
//		public void canGetAllFeedbacks() throws Exception {
//			Feedback feedbackA = DataUtil.createFeedbackA(accountService, 
//					tourPackageService, 
//					packageInclusionService,
//					bookingService,
//					feedbackCategoryService);
//		    
//		    Feedback feedbackB = DataUtil.createFeedbackB(accountService, 
//					tourPackageService, 
//					packageInclusionService,
//					bookingService,
//					feedbackCategoryService);
//		    
//		    FeedbackResponse savedFeedbackA = feedbackService.addFeedback(feedbackA);
// 		    FeedbackResponse savedFeedbackB = feedbackService.addFeedback(feedbackB);
//			
//			mockMvc.perform(
//					MockMvcRequestBuilders.get("/feedback")
//						.contentType(MediaType.APPLICATION_JSON)
//			).andExpect(
//					MockMvcResultMatchers.status().isOk()
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$[0].feedbackId").value(savedFeedbackA.getFeedbackId())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$[0].account.accountId").value(savedFeedbackA.getAccount().getAccountId())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$[0].booking.bookingId").value(savedFeedbackA.getBooking().getBookingId())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$[0].category").value(savedFeedbackA.getCategory())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$[0].stars").value(savedFeedbackA.getStars())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$[0].comment").value(savedFeedbackA.getComment())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$[0].suggestion").value(savedFeedbackA.getSuggestion())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$[0].status").value(FeedbackStatus.NEW.toString())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$[1].feedbackId").value(savedFeedbackB.getFeedbackId())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$[1].account.accountId").value(savedFeedbackB.getAccount().getAccountId())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$[1].booking.bookingId").value(savedFeedbackB.getBooking().getBookingId())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$[1].category").value(savedFeedbackB.getCategory())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$[1].stars").value(savedFeedbackB.getStars())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$[1].comment").value(savedFeedbackB.getComment())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$[1].suggestion").value(savedFeedbackB.getSuggestion())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$[1].status").value(FeedbackStatus.NEW.toString())
//			);
//		}
		
		@Test
		public void canGetFeedbackByCategory() throws Exception {
			
		}
		
		@Test
		public void canUpdateFeedbackStars() throws Exception {
			Feedback feedbackA = DataUtil.createFeedbackA(accountService, 
					tourPackageService, 
					packageInclusionService,
					bookingService,
					feedbackCategoryService);
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
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.status").value(FeedbackStatus.NEW.toString())
			);
		}
		
		@Test
		public void canUpdateFeedbackStatus() throws Exception {
			Feedback feedbackA = DataUtil.createFeedbackA(accountService, 
					tourPackageService, 
					packageInclusionService,
					bookingService,
					feedbackCategoryService);
		    FeedbackResponse savedFeedbackA = feedbackService.addFeedback(feedbackA);
		    
		    Feedback newFeedback = new Feedback();
		    newFeedback.setStatus(FeedbackStatus.VIEWED);
		    
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
	        		MockMvcResultMatchers.jsonPath("$.stars").value(savedFeedbackA.getStars())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.comment").value(savedFeedbackA.getComment())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.suggestion").value(savedFeedbackA.getSuggestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.status").value(newFeedback.getStatus().toString())
			);
		}
		
		@Test
		public void canDeleteFeedback() throws Exception {
			Feedback feedbackA = DataUtil.createFeedbackA(accountService, 
					tourPackageService, 
					packageInclusionService,
					bookingService,
					feedbackCategoryService);
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
			Feedback feedbackA = DataUtil.createFeedbackA(accountService, 
					tourPackageService, 
					packageInclusionService,
					bookingService,
					feedbackCategoryService);
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
			Feedback feedbackA = DataUtil.createFeedbackA(accountService, 
					tourPackageService, 
					packageInclusionService,
					bookingService,
					feedbackCategoryService);
		    
			Account accountB = DataUtil.createAccountB();
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
	    	Feedback feedbackA = DataUtil.createFeedbackA(accountService, 
					tourPackageService, 
					packageInclusionService,
					bookingService,
					feedbackCategoryService);
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
	    	Feedback feedbackA = DataUtil.createFeedbackA(accountService, 
					tourPackageService, 
					packageInclusionService,
					bookingService,
					feedbackCategoryService);
		    feedbackService.addFeedback(feedbackA);
		    
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
	    	Feedback feedbackA = DataUtil.createFeedbackA(accountService, 
					tourPackageService, 
					packageInclusionService,
					bookingService,
					feedbackCategoryService);
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
