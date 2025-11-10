package com.example.geco.controllers;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.geco.DataUtil;
import com.example.geco.domains.FeedbackCategory;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FeedbackCategoryControllerTests extends AbstractControllerTest {
	@Nested
    class SuccessTests {
		@Test
		public void canAddFeedbackCategory() throws Exception {
			FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
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
			);
		}

		@Test
		public void canGetFeedbackCategory() throws Exception {
			FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			feedbackCategoryRepository.save(categoryA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback-category/" + categoryA.getFeedbackCategoryId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.label").value(categoryA.getLabel())
			);
		}

		@Test
		public void canGetAllFeedbackCategories() throws Exception {
			FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			feedbackCategoryRepository.save(categoryA);
			
			FeedbackCategory categoryB = DataUtil.createFeedbackCategoryB();
			feedbackCategoryRepository.save(categoryB);
			
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
		public void canGetAllFeedbackCategoriesEmpty() throws Exception {
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
		public void canUpdateFeedbackCategory() throws Exception {
			FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			FeedbackCategory savedCategoryA = feedbackCategoryRepository.save(categoryA);
			
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
		public void canDeleteFeedbackCategory() throws Exception {
			FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			FeedbackCategory savedCategoryA = feedbackCategoryRepository.save(categoryA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/feedback-category/" + savedCategoryA.getFeedbackCategoryId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.feedbackCategoryId").exists()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.label").value(savedCategoryA.getLabel())
			);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback-category/" + savedCategoryA.getFeedbackCategoryId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			);
		}
	}
	
	@Nested
    class FailureTests {
		@Test
		public void cannotAddFeedbackCategoryImproperLabel() throws Exception {
			FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			categoryA.setLabel("");
			
			String categoryJson = objectMapper.writeValueAsString(categoryA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/feedback-category")
						.contentType(MediaType.APPLICATION_JSON)
						.content(categoryJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Label must at least have 1 character.")
			);
		}
		
		@Test
		public void cannotAddFeedbackCategoryAlreadyExist() throws Exception {
			FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			feedbackCategoryRepository.save(categoryA);
			
			String attractionJson = objectMapper.writeValueAsString(categoryA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/feedback-category")
						.contentType(MediaType.APPLICATION_JSON)
						.content(attractionJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Label \"" + categoryA.getLabel() + "\" already exist.")
			);
		}
		
		@Test
		public void cannotGetFeedbackCategory() throws Exception {
			int id = 0;
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/feedback-category/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Feedback Category with ID " + id + " not found.")
			);
		}
		
		@Test
		public void cannotUpdateFeedbackCategoryNotFound() throws Exception {
			FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
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
	        		MockMvcResultMatchers.jsonPath("$.error").value("Feedback category \"" + categoryA.getLabel() + "\" not found.")
			);
		}

		@Test
		public void cannotUpdateFeedbackCategoryImproperLabel() throws Exception {
			FeedbackCategory categoryA = DataUtil.createFeedbackCategoryA();
			FeedbackCategory savedCategoryA = feedbackCategoryRepository.save(categoryA);
			
			categoryA.setLabel("");
			String categoryJson = objectMapper.writeValueAsString(categoryA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/feedback-category/" + savedCategoryA.getFeedbackCategoryId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(categoryJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Label must at least have 1 character.")
			);
		}
		
		@Test
		public void cannotDeleteFeedbackCategoryNotFound() throws Exception {
			int id = 0;
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/feedback-category/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Feedback category with ID " + id + " not found.")
			);
		}
	}
}
