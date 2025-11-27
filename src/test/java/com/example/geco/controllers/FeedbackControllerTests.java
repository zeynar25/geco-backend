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
import com.example.geco.domains.Feedback.FeedbackStatus;
import com.example.geco.dto.FeedbackRequest;
import com.example.geco.dto.FeedbackResponse;
import com.example.geco.dto.FeedbackUpdateRequest;
import com.example.geco.dto.UserFeedbackUpdateRequest;


// Implement GET success and failure tests

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FeedbackControllerTests extends AbstractControllerTest {
	@Nested
    class SuccessTests {
		@Test
		public void canAddFeedback() throws Exception {
			Account savedAccount = DataUtil.createUserAccountA(accountRepository);
		    mockUserAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
			String requestJson = objectMapper.writeValueAsString(request);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/feedback")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
			).andExpect(
					MockMvcResultMatchers.status().isCreated()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackId").exists()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.account.accountId").value(savedAccount.getAccountId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.booking.bookingId").value(request.getBookingId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.category").exists()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.stars").value(request.getStars())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.comment").value(request.getComment())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.suggestion").value(request.getSuggestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackStatus").value(FeedbackStatus.NEW.toString())
			);
		}
		
		@Test
		public void canGetFeedback() throws Exception {
//			Feedback feedbackA = DataUtil.createFeedbackA(accountService, 
//					tourPackageService, 
//					packageInclusionService,
//					bookingService,
//					feedbackCategoryService);
//		    FeedbackResponse savedFeedbackA = feedbackService.addFeedback(feedbackA);
//		    
//		    mockMvc.perform(
//					MockMvcRequestBuilders.get("/feedback/" + savedFeedbackA.getFeedbackId())
//						.contentType(MediaType.APPLICATION_JSON)
//			).andExpect(
//					MockMvcResultMatchers.status().isOk()
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$.feedbackId").value(savedFeedbackA.getFeedbackId())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$.account.accountId").value(savedFeedbackA.getAccount().getAccountId())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$.booking.bookingId").value(savedFeedbackA.getBooking().getBookingId())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$.category").value(savedFeedbackA.getCategory())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$.stars").value(savedFeedbackA.getStars())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$.comment").value(savedFeedbackA.getComment())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$.suggestion").value(savedFeedbackA.getSuggestion())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$.status").value(FeedbackStatus.NEW.toString())
//			);
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
		
	
		public void canGetMyFeedbacks() throws Exception {
		    
		}

	
		@Test
		public void canGetFeedbackByCategory() throws Exception {
			
		}
	
		@Test
		public void canGetFeedbackByCategoryAndDateBetween() throws Exception {
			
		}
		
		@Test
		public void canGetActiveFeedbacks() throws Exception {
			
		}
		
		@Test
		public void canGetInctiveFeedbacks() throws Exception {
			
		}
		
		@Test
		public void canUpdateFeedbackStars() throws Exception {
			Account savedAccount = DataUtil.createUserAccountA(accountRepository);
		    mockUserAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedback = feedbackService.addFeedback(request);
		    
		    UserFeedbackUpdateRequest updateRequest = UserFeedbackUpdateRequest.builder()
		    		.stars(2.0)
		    		.build();
		    
			String requestJson = objectMapper.writeValueAsString(updateRequest);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/" + savedFeedback.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackId").value(savedFeedback.getFeedbackId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.account.accountId").value(savedFeedback.getAccount().getAccountId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.booking.bookingId").value(savedFeedback.getBooking().getBookingId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.category").value(savedFeedback.getCategory())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.stars").value(updateRequest.getStars())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.comment").value(savedFeedback.getComment())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.suggestion").value(savedFeedback.getSuggestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackStatus").value(savedFeedback.getFeedbackStatus().toString())
			);
		}
		
		@Test
		public void canUpdateFeedbackComment() throws Exception {
			Account savedAccount = DataUtil.createUserAccountA(accountRepository);
		    mockUserAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedback = feedbackService.addFeedback(request);
		    
		    UserFeedbackUpdateRequest updateRequest = UserFeedbackUpdateRequest.builder()
		    		.comment("this is a new comment")
		    		.build();
		    
			String requestJson = objectMapper.writeValueAsString(updateRequest);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/" + savedFeedback.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackId").value(savedFeedback.getFeedbackId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.account.accountId").value(savedFeedback.getAccount().getAccountId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.booking.bookingId").value(savedFeedback.getBooking().getBookingId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.category").value(savedFeedback.getCategory())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.stars").value(savedFeedback.getStars())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.comment").value(updateRequest.getComment())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.suggestion").value(savedFeedback.getSuggestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackStatus").value(savedFeedback.getFeedbackStatus().toString())
			);
		}
		
		@Test
		public void canUpdateFeedbackSuggestion() throws Exception {
			Account savedAccount = DataUtil.createUserAccountA(accountRepository);
		    mockUserAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedback = feedbackService.addFeedback(request);
		    
		    UserFeedbackUpdateRequest updateRequest = UserFeedbackUpdateRequest.builder()
		    		.suggestion("This is a new suggestion")
		    		.build();
		    
			String requestJson = objectMapper.writeValueAsString(updateRequest);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/" + savedFeedback.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackId").value(savedFeedback.getFeedbackId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.account.accountId").value(savedFeedback.getAccount().getAccountId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.booking.bookingId").value(savedFeedback.getBooking().getBookingId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.category").value(savedFeedback.getCategory())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.stars").value(savedFeedback.getStars())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.comment").value(savedFeedback.getComment())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.suggestion").value(updateRequest.getSuggestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackStatus").value(savedFeedback.getFeedbackStatus().toString())
			);
		}
		
		@Test
		public void canUpdateFeedbackStatus() throws Exception {
			Account savedAccount = DataUtil.createStaffAccountA(accountRepository);
		    mockStaffAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedback = feedbackService.addFeedback(request);
		    
		    FeedbackUpdateRequest updateRequest = FeedbackUpdateRequest.builder()
		    		.feedbackStatus(FeedbackStatus.VIEWED)
		    		.build();
		    
			String requestJson = objectMapper.writeValueAsString(updateRequest);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/staff/" + savedFeedback.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackId").value(savedFeedback.getFeedbackId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.account.accountId").value(savedFeedback.getAccount().getAccountId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.booking.bookingId").value(savedFeedback.getBooking().getBookingId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.category").value(savedFeedback.getCategory())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.stars").value(savedFeedback.getStars())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.comment").value(savedFeedback.getComment())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.suggestion").value(savedFeedback.getSuggestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackStatus").value(updateRequest.getFeedbackStatus().toString())
			);
		}
		
		@Test
		public void canSoftDeleteFeedback() throws Exception {
			Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedback = feedbackService.addFeedback(request);
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/feedback/" + savedFeedback.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNoContent()
			);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback/" + savedFeedback.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackId").value(savedFeedback.getFeedbackId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.account.accountId").value(savedFeedback.getAccount().getAccountId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.booking.bookingId").value(savedFeedback.getBooking().getBookingId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.category").value(savedFeedback.getCategory())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.stars").value(savedFeedback.getStars())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.comment").value(savedFeedback.getComment())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.suggestion").value(savedFeedback.getSuggestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackStatus").value(savedFeedback.getFeedbackStatus().toString())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.active").value("false")
			);
		}
		
		@Test
		public void canRestoreFeedback() throws Exception {
			Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedback = feedbackService.addFeedback(request);
		    feedbackService.softDeleteFeedback(savedFeedback.getFeedbackId());
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/restore/" + savedFeedback.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNoContent()
			);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback/" + savedFeedback.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackId").value(savedFeedback.getFeedbackId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.account.accountId").value(savedFeedback.getAccount().getAccountId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.booking.bookingId").value(savedFeedback.getBooking().getBookingId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.category").value(savedFeedback.getCategory())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.stars").value(savedFeedback.getStars())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.comment").value(savedFeedback.getComment())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.suggestion").value(savedFeedback.getSuggestion())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackStatus").value(savedFeedback.getFeedbackStatus().toString())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.active").value("true")
			);
		}
	}
	
	@Nested
    class FailureTests {
		@Test
		public void cannotAddFeedbackNullBooking() throws Exception {
			Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    request.setBookingId(null);
			String feedbackJson = objectMapper.writeValueAsString(request);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/feedback")
						.contentType(MediaType.APPLICATION_JSON)
						.content(feedbackJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Booking ID is required")
			);
		}
		
		@Test
		public void cannotAddFeedbackNullCategory() throws Exception {
			Account savedAccount = DataUtil.createUserAccountA(accountRepository);
		    mockUserAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    request.setCategoryId(null);
			String feedbackJson = objectMapper.writeValueAsString(request);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/feedback")
						.contentType(MediaType.APPLICATION_JSON)
						.content(feedbackJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Feedback Category ID is required")
			);
		}
		
		@Test
		public void cannotAddFeedbackInvalidStarsExceed() throws Exception {
			Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    request.setStars(6.0);
			String feedbackJson = objectMapper.writeValueAsString(request);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/feedback")
						.contentType(MediaType.APPLICATION_JSON)
						.content(feedbackJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Stars cannot exceed 5")
			);
		}
		
		@Test
		public void cannotAddFeedbackInvalidStarsAtleast() throws Exception {
			Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    request.setStars(-1.0);
			String feedbackJson = objectMapper.writeValueAsString(request);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/feedback")
						.contentType(MediaType.APPLICATION_JSON)
						.content(feedbackJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Stars must be at least 0")
			);
		}
		
//		@Test
//		public void cannotGetFeedback() throws Exception {
//			int id = 0;
//		    
//		    mockMvc.perform(
//					MockMvcRequestBuilders.get("/feedback/" + id)
//						.contentType(MediaType.APPLICATION_JSON)
//			).andExpect(
//					MockMvcResultMatchers.status().isNotFound()
//			).andExpect(
//					MockMvcResultMatchers.jsonPath("$.error").value("Feedback with ID \"" + id + "\" not found.")
//		    );
//		}
	    
	    @Test
		public void cannotUpdateFeedbackMissingFields() throws Exception {
	    	Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedbackA = feedbackService.addFeedback(request);
		    UserFeedbackUpdateRequest newFeedback = UserFeedbackUpdateRequest.builder().build();

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
	    	Account savedAccount = DataUtil.createStaffAccountA(accountRepository);
		    mockStaffAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
	    	UserFeedbackUpdateRequest newFeedback = UserFeedbackUpdateRequest.builder()
	    			.stars(3.4)
	    			.build();
	    	
			String feedbackJson = objectMapper.writeValueAsString(newFeedback);
			
			int id = 69;
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(feedbackJson)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Feedback with ID '" + id + "' not found.")
			);
		}
	    
	    @Test
		public void cannotUpdateFeedbackInvalidStarsExceeded() throws Exception {
	    	Account savedAccount = DataUtil.createStaffAccountA(accountRepository);
		    mockStaffAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedbackA = feedbackService.addFeedback(request);
		    
		    UserFeedbackUpdateRequest newFeedback = UserFeedbackUpdateRequest.builder()
		    		.stars(5.5)
		    		.build();
		    
			String feedbackJson = objectMapper.writeValueAsString(newFeedback);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/" + savedFeedbackA.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(feedbackJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Stars cannot exceed 5")
			);
	    }
	    
	    @Test
		public void cannotUpdateFeedbackInvalidStarsAtLeast() throws Exception {
	    	Account savedAccount = DataUtil.createStaffAccountA(accountRepository);
		    mockStaffAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedbackA = feedbackService.addFeedback(request);
		    
		    UserFeedbackUpdateRequest newFeedback = UserFeedbackUpdateRequest.builder()
		    		.stars(-1.5)
		    		.build();
		    
			String feedbackJson = objectMapper.writeValueAsString(newFeedback);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/" + savedFeedbackA.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(feedbackJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Stars must be at least 0")
			);
		}

	    @Test
		public void cannotUpdateFeedbackInvalidComment() throws Exception {
	    	Account savedAccount = DataUtil.createStaffAccountA(accountRepository);
		    mockStaffAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedbackA = feedbackService.addFeedback(request);
		    
		    UserFeedbackUpdateRequest newFeedback = UserFeedbackUpdateRequest.builder()
		    		.comment("short")
		    		.build();
		    
			String feedbackJson = objectMapper.writeValueAsString(newFeedback);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/" + savedFeedbackA.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(feedbackJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Comment must be at least 10 characters")
			);
		}

	    @Test
		public void cannotUpdateFeedbackInvalidSuggestion() throws Exception {
	    	Account savedAccount = DataUtil.createStaffAccountA(accountRepository);
		    mockStaffAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedbackA = feedbackService.addFeedback(request);
		    
		    UserFeedbackUpdateRequest newFeedback = UserFeedbackUpdateRequest.builder()
		    		.suggestion("short")
		    		.build();
		    
			String feedbackJson = objectMapper.writeValueAsString(newFeedback);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/" + savedFeedbackA.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(feedbackJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Suggestion must be at least 10 characters")
			);
		}
	    
	    @Test
		public void cannotSoftDeleteFeedbackNotFound() throws Exception {
	    	Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
			int id = 0;
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.delete("/feedback/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Feedback with ID '" + id + "' not found.")
		    );
		}
	    
	    @Test
		public void cannotSoftDeleteFeedbackAsUser() throws Exception {
	    	Account savedAccount = DataUtil.createUserAccountA(accountRepository);
		    mockUserAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedback = feedbackService.addFeedback(request);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.delete("/feedback/" + savedFeedback.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isForbidden()
			);
		}
	    
	    @Test
		public void cannotSoftDeleteFeedbackAsStaff() throws Exception {
	    	Account savedAccount = DataUtil.createStaffAccountA(accountRepository);
		    mockStaffAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedback = feedbackService.addFeedback(request);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.delete("/feedback/" + savedFeedback.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isForbidden()
			);
		}
	    
	    @Test
		public void cannotSoftDeleteInactiveFeedback() throws Exception {
	    	Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedback = feedbackService.addFeedback(request);
		    feedbackService.softDeleteFeedback(savedFeedback.getFeedbackId());
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.delete("/feedback/" + savedFeedback.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Account is already disabled.")
			);
		}
	   
	    @Test
		public void cannotRestoreFeedbackNotFound() throws Exception {
	    	Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
			int id = 0;
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/restore/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Feedback with ID '" + id + "' not found.")
		    );
		}
	    
	    @Test
		public void cannotRestoreFeedbackAsUser() throws Exception {
	    	Account savedAccount = DataUtil.createUserAccountA(accountRepository);
		    mockUserAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedback = feedbackService.addFeedback(request);
		    
		    Feedback copyFeedback = Feedback.builder()
		    		.feedbackId(savedFeedback.getFeedbackId())
		    		.account(savedFeedback.getAccount())
		    		.booking(savedFeedback.getBooking())
		    		.stars(savedFeedback.getStars())
		    		.comment(savedFeedback.getComment())
		    		.suggestion(savedFeedback.getSuggestion())
		    		.isActive(false)
		    		.build();
		    
		    feedbackRepository.save(copyFeedback);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/restore/" + savedFeedback.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isForbidden()
			);
		}
	    
	    @Test
		public void cannotRestoreFeedbackAsStaff() throws Exception {
	    	Account savedAccount = DataUtil.createStaffAccountA(accountRepository);
		    mockStaffAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedback = feedbackService.addFeedback(request);
		    
		    Feedback copyFeedback = Feedback.builder()
		    		.feedbackId(savedFeedback.getFeedbackId())
		    		.account(savedFeedback.getAccount())
		    		.booking(savedFeedback.getBooking())
		    		.stars(savedFeedback.getStars())
		    		.comment(savedFeedback.getComment())
		    		.suggestion(savedFeedback.getSuggestion())
		    		.isActive(false)
		    		.build();
		    
		    feedbackRepository.save(copyFeedback);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/restore/" + savedFeedback.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isForbidden()
			);
	    }
	    
	    @Test
		public void cannotRestoreFeedbackActiveFeedback() throws Exception {
	    	Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
		    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		    
		    FeedbackRequest request = DataUtil.createFeedbackRequestA(
		    		savedAccount.getAccountId(),
		    		accountRepository,
		    		packageInclusionRepository,
		    		tourPackageRepository,
		    		bookingRepository,
		    		feedbackCategoryRepository);
		    
		    FeedbackResponse savedFeedback = feedbackService.addFeedback(request);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback/restore/" + savedFeedback.getFeedbackId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Account is already active.")
			);
		}
	}
}
