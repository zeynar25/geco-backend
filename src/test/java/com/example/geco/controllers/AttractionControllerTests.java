package com.example.geco.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.geco.AbstractControllerTest;
import com.example.geco.DataUtil;
import com.example.geco.domains.Attraction;
import com.example.geco.dto.AttractionResponse;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AttractionControllerTests extends AbstractControllerTest{
	@Nested
    class SuccessTests {
 		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canAddAttraction() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			Attraction attractionA = DataUtil.createAttractionA();
			String attractionJson = objectMapper.writeValueAsString(attractionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/attraction")
						.contentType(MediaType.APPLICATION_JSON)
						.content(attractionJson)
			).andExpect(
					MockMvcResultMatchers.status().isCreated()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.name").value(attractionA.getName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.description").value(attractionA.getDescription())
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canGetAttraction() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			Attraction attractionA = DataUtil.createAttractionA();
			AttractionResponse savedAttractionA = attractionService.addAttraction(attractionA);

	        mockMvc.perform(
	                MockMvcRequestBuilders.get("/attraction/" + savedAttractionA.getAttractionId())
	                    .contentType(MediaType.APPLICATION_JSON)
	        )
	        .andExpect(
	        		MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.name").value(attractionA.getName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.description").value(attractionA.getDescription())
			);
		}

		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canGetAllAttraction() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			Attraction attractionA = DataUtil.createAttractionA();
			AttractionResponse savedAttractionA = attractionService.addAttraction(attractionA);

			Attraction attractionB = DataUtil.createAttractionB();
			AttractionResponse savedAttractionB = attractionService.addAttraction(attractionB);
			
			mockMvc.perform(
	                MockMvcRequestBuilders.get("/attraction")
	                    .contentType(MediaType.APPLICATION_JSON)
	        ).andExpect(
	        		MockMvcResultMatchers.status().isOk()
	        ).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2))
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].name").value(savedAttractionA.getName())
			)
	        .andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].description").value(savedAttractionA.getDescription())
	        ).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].name").value(savedAttractionB.getName())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].description").value(savedAttractionB.getDescription())
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canUpdateAttraction() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			Attraction attractionA = DataUtil.createAttractionA();
			attractionService.addAttraction(attractionA);
			
			attractionA.setName("Ang Ina ng Kalikasan");
		    String attractionJson = objectMapper.writeValueAsString(attractionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/attraction/" + attractionA.getAttractionId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(attractionJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
				MockMvcResultMatchers.jsonPath("$.attractionId").exists()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.name").value(attractionA.getName())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.description").value(attractionA.getDescription())
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canSoftDeleteAttraction() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			Attraction attractionA = DataUtil.createAttractionA();
			AttractionResponse savedAttractionA = attractionService.addAttraction(attractionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/attraction/" + savedAttractionA.getAttractionId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNoContent()
			);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/attraction/" + savedAttractionA.getAttractionId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk() // because it's still there, just inactive.
			);
		}

		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canHardDeleteAttraction() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			Attraction attractionA = DataUtil.createAttractionA();
			AttractionResponse savedAttractionA = attractionService.addAttraction(attractionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/attraction/" + savedAttractionA.getAttractionId())
						.param("soft", "false")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNoContent()
			);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/attraction/" + savedAttractionA.getAttractionId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void canGetNumberOfAttractions() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			Attraction attractionA = DataUtil.createAttractionA();
			Attraction attractionB = DataUtil.createAttractionB();

			attractionService.addAttraction(attractionA);
			attractionService.addAttraction(attractionB);
			

			double n = attractionService.getAttractionsNumber();
			
			assertEquals(2, n);
		}
 	}
 
 	@Nested
    class FailureTests {
	 	@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotAddAttractionImproperTitle() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			Attraction attraction = DataUtil.createAttractionA();
		    attraction.setName(""); // invalid title
		    String attractionJson = objectMapper.writeValueAsString(attraction);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.post("/attraction")
						.contentType(MediaType.APPLICATION_JSON)
						.content(attractionJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Attraction name must have at least 1 character.")
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotAddAttractionImproperDescription() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			Attraction attraction = DataUtil.createAttractionA();
		    attraction.setName("valid title"); 
		    attraction.setDescription("too short"); // invalid description
		    String attractionJson = objectMapper.writeValueAsString(attraction);
		    
		    mockMvc.perform(
					MockMvcRequestBuilders.post("/attraction")
						.contentType(MediaType.APPLICATION_JSON)
						.content(attractionJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Attraction description must be at least 10 characters long.")
			);
		}
		
		@Test
		@WithMockUser(username = "guest@email.com", roles = "GUEST")
		public void cannotAddAttractionAsGuest() throws Exception {
			mockGuestAuthentication("guest@email.com");
			
			Attraction attractionA = DataUtil.createAttractionA();
			String attractionJson = objectMapper.writeValueAsString(attractionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/attraction")
						.contentType(MediaType.APPLICATION_JSON)
						.content(attractionJson)
			).andExpect(
					MockMvcResultMatchers.status().isForbidden()
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotGetAttraction() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			Attraction attractionA = DataUtil.createAttractionA();
			attractionA.setAttractionId(0);

	        mockMvc.perform(
	                MockMvcRequestBuilders.get("/attraction/" + attractionA.getAttractionId())
	                    .contentType(MediaType.APPLICATION_JSON)
	        )
	        .andExpect(
	        		MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Attraction with ID '"+ attractionA.getAttractionId() + "' not found.")
			);
		}

		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotUpdateAttractionImproperName() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			Attraction attractionA = DataUtil.createAttractionA();
			attractionService.addAttraction(attractionA);
			
			attractionA.setName("");
		    String attractionJson = objectMapper.writeValueAsString(attractionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/attraction/" + attractionA.getAttractionId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(attractionJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
				MockMvcResultMatchers.jsonPath("$.error").value("Attraction name must have at least 1 character.")
			);
		}

		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotUpdateAttractionImproperDescription() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			Attraction attractionA = DataUtil.createAttractionA();
			attractionService.addAttraction(attractionA);
			
			attractionA.setDescription("too short");
		    String attractionJson = objectMapper.writeValueAsString(attractionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/attraction/" + attractionA.getAttractionId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(attractionJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
				MockMvcResultMatchers.jsonPath("$.error").value("Attraction description must be at least 10 characters long.")
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotUpdateAttractionMissing() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			Attraction attractionA = DataUtil.createAttractionA();
			attractionA.setAttractionId(0);
		    String attractionJson = objectMapper.writeValueAsString(attractionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/attraction/" + attractionA.getAttractionId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(attractionJson)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
				MockMvcResultMatchers.jsonPath("$.error").value("Attraction with ID '"+ attractionA.getAttractionId() + "' not found.")
			);
		}
		
		@Test
		@WithMockUser(username = "guest@email.com", roles = "GUEST")
		public void cannotUpdateAttractionAsGuest() throws Exception {
			mockGuestAuthentication("guest@email.com");
			
			Attraction attractionA = DataUtil.createAttractionA();
			attractionService.addAttraction(attractionA);
			
			attractionA.setName("Ang Ina ng Kalikasan");
		    String attractionJson = objectMapper.writeValueAsString(attractionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/attraction/" + attractionA.getAttractionId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(attractionJson)
			).andExpect(
					MockMvcResultMatchers.status().isForbidden()
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = "ADMIN")
		public void cannotDeleteAttraction() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			int id = 1;
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/attraction/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Attraction with ID '"+ id + "' not found.")
			);
		}

		@Test
		@WithMockUser(username = "guest@email.com", roles = "GUEST")
		public void cannotDeleteAttractionAsGuest() throws Exception {
			mockGuestAuthentication("guest@email.com");
			
			Attraction attractionA = DataUtil.createAttractionA();
			AttractionResponse savedAttractionA = attractionService.addAttraction(attractionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/attraction/" + savedAttractionA.getAttractionId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isForbidden()
			);
		}
	 
 	}
}
