package com.example.geco.controllers;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.geco.AbstractControllerTest;
import com.example.geco.DataUtil;
import com.example.geco.domains.FeedbackCategory;
import com.example.geco.dto.FeedbackCategoryRequest;
import com.example.geco.repositories.FeedbackCategoryRepository;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FeedbackCategoryControllerTests extends AbstractControllerTest {
	@Nested
    class SuccessTests {
		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAFF")
		public void canAddFeedbackCategory() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			FeedbackCategoryRequest categoryA = DataUtil.createFeedbackCategoryRequestA();
			String categoryJson = objectMapper.writeValueAsString(categoryA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/feedback-category")
						.contentType(MediaType.APPLICATION_JSON)
						.content(categoryJson)
			).andExpect(
					MockMvcResultMatchers.status().isCreated()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackCategoryId").exists()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.label").value(categoryA.getLabel())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.active").value("true")
			);
		}

		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAFF")
		public void canGetFeedbackCategory() throws Exception {
			mockStaffAuthentication(2, "staff@email.com");
			
			FeedbackCategoryRequest categoryA = DataUtil.createFeedbackCategoryRequestA();
			FeedbackCategory savedCategory = feedbackCategoryService.addCategory(categoryA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback-category/" + savedCategory.getFeedbackCategoryId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.label").value(categoryA.getLabel())
			);
		}

		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAFF")
		public void canGetAllFeedbackCategories() throws Exception {
			mockStaffAuthentication(2, "staff@email.com");
			
			FeedbackCategoryRequest categoryA = DataUtil.createFeedbackCategoryRequestA();
			feedbackCategoryService.addCategory(categoryA);
			
			FeedbackCategoryRequest categoryB = DataUtil.createFeedbackCategoryRequestB();
			feedbackCategoryService.addCategory(categoryB);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback-category")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].label").value(categoryA.getLabel())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].label").value(categoryB.getLabel())
			);
		}
		
		@Test
		@WithMockUser(username = "user@email.com", roles = "USER")
		public void canGetAllActiveFeedbackCategories() throws Exception {
			mockUserAuthentication(3, "user@email.com");
			
			FeedbackCategoryRequest categoryA = DataUtil.createFeedbackCategoryRequestA();
			FeedbackCategory savedCategoryA = feedbackCategoryService.addCategory(categoryA);
			
			savedCategoryA.setActive(false);
			feedbackCategoryRepository.save(savedCategoryA);
			
			FeedbackCategoryRequest categoryB = DataUtil.createFeedbackCategoryRequestB();
			feedbackCategoryService.addCategory(categoryB);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback-category/active")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].label").value(categoryB.getLabel())
			);
		}

		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAFF")
		public void canGetAllInactiveFeedbackCategories() throws Exception {
			mockStaffAuthentication(2, "staff@email.com");
			
			FeedbackCategoryRequest categoryA = DataUtil.createFeedbackCategoryRequestA();
			FeedbackCategory savedCategoryA = feedbackCategoryService.addCategory(categoryA);
			
			savedCategoryA.setActive(false);
			feedbackCategoryRepository.save(savedCategoryA);
			
			FeedbackCategoryRequest categoryB = DataUtil.createFeedbackCategoryRequestB();
			feedbackCategoryService.addCategory(categoryB);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback-category/inactive")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].label").value(categoryA.getLabel())
			);
		}
		
		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAFF")
		public void canGetAllFeedbackCategoriesEmpty() throws Exception {
			mockStaffAuthentication(2, "staff@email.com");
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback-category")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$").isEmpty()
			);
		}

		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAFF")
		public void canUpdateFeedbackCategory() throws Exception {
			mockStaffAuthentication(2, "staff@email.com");
			
			FeedbackCategoryRequest categoryA = DataUtil.createFeedbackCategoryRequestA();
			FeedbackCategory savedCategoryA = feedbackCategoryService.addCategory(categoryA);
			
			categoryA.setLabel("new label");
			String categoryJson = objectMapper.writeValueAsString(categoryA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback-category/" + savedCategoryA.getFeedbackCategoryId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(categoryJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackCategoryId").exists()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.label").value(categoryA.getLabel())
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canSoftDeleteFeedbackCategory() throws Exception {
			mockAdminAuthentication(2, "admin@email.com");
			
			FeedbackCategoryRequest categoryA = DataUtil.createFeedbackCategoryRequestA();
			FeedbackCategory savedCategoryA = feedbackCategoryService.addCategory(categoryA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/feedback-category/admin/" + savedCategoryA.getFeedbackCategoryId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNoContent()
			);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback-category/" + savedCategoryA.getFeedbackCategoryId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.active").value("false")
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canRestoreFeedbackCategory() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			FeedbackCategoryRequest categoryA = DataUtil.createFeedbackCategoryRequestA();
			FeedbackCategory savedCategoryA = feedbackCategoryService.addCategory(categoryA);
			
			feedbackCategoryService.softDeleteCategory(savedCategoryA.getFeedbackCategoryId());
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback-category/admin/restore/" + savedCategoryA.getFeedbackCategoryId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNoContent()
			);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback-category/" + savedCategoryA.getFeedbackCategoryId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.active").value("true")
			);
		}
	}
	
	@Nested
    class FailureTests {
		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAFF")
		public void cannotAddFeedbackCategoryImproperLabel() throws Exception {
			mockStaffAuthentication(2, "staff@email.com");
			
			FeedbackCategoryRequest categoryA = DataUtil.createFeedbackCategoryRequestA();
			categoryA.setLabel("");
			
			String categoryJson = objectMapper.writeValueAsString(categoryA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/feedback-category")
						.contentType(MediaType.APPLICATION_JSON)
						.content(categoryJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Label is required")
			);
		}
		
		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAFF")
		public void cannotAddFeedbackCategoryAlreadyExist() throws Exception {
			mockStaffAuthentication(2, "staff@email.com");
			
			FeedbackCategoryRequest categoryA = DataUtil.createFeedbackCategoryRequestA();
			feedbackCategoryService.addCategory(categoryA);
			
			String attractionJson = objectMapper.writeValueAsString(categoryA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/feedback-category")
						.contentType(MediaType.APPLICATION_JSON)
						.content(attractionJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Label '" + categoryA.getLabel() + "' already exist.")
			);
		}
		
		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAFF")
		public void cannotGetFeedbackCategory() throws Exception {
			mockStaffAuthentication(2, "staff@email.com");
			
			int id = 0;
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback-category/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Feedback category with ID '" + id + "' not found.")
			);
		}
		
		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAFF")
		public void cannotUpdateFeedbackCategoryNotFound() throws Exception {
			mockStaffAuthentication(2, "staff@email.com");
			
			FeedbackCategoryRequest categoryA = DataUtil.createFeedbackCategoryRequestA();
			// did not save categoryA to database.
			int id = 0;
			
			categoryA.setLabel("new label");
			String categoryJson = objectMapper.writeValueAsString(categoryA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback-category/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(categoryJson)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Feedback category with ID '" + id + "' not found.")
			);
		}

		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAFF")
		public void cannotUpdateFeedbackCategoryImproperLabel() throws Exception {
			mockStaffAuthentication(2, "staff@email.com");
			
			FeedbackCategoryRequest categoryA = DataUtil.createFeedbackCategoryRequestA();
			FeedbackCategory savedCategoryA = feedbackCategoryService.addCategory(categoryA);
			
			categoryA.setLabel("");
			String categoryJson = objectMapper.writeValueAsString(categoryA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback-category/" + savedCategoryA.getFeedbackCategoryId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(categoryJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Label is required")
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotDeleteFeedbackCategoryNotFound() throws Exception {
			mockAdminAuthentication(2, "admin@email.com");
			
			int id = 0;
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/feedback-category/admin/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Feedback category with ID '" + id + "' not found.")
			);
		}
	}
}
