package com.example.geco.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.geco.AbstractControllerTest;
import com.example.geco.DataUtil;
import com.example.geco.domains.Attraction;
import com.example.geco.dto.AttractionResponse;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc(addFilters = false) // disables spring security for attraction controller tests.
public class AttractionControllerTests extends AbstractControllerTest{
	@Nested
    class SuccessTests {
 		@Test
		public void canAddAttraction() throws Exception {
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
		public void canGetAttraction() throws Exception {
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
		public void canGetAllAttraction() throws Exception {
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
		public void canUpdateAttraction() throws Exception {
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
		public void canDeleteAttraction() throws Exception {
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
					MockMvcResultMatchers.status().isNotFound()
			);
		}
		
		@Test
		public void canGetNumberOfAttractions() throws Exception {
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
		public void cannotAddAttractionImproperTitle() throws Exception {
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
		public void cannotAddAttractionImproperDescription() throws Exception {
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
		public void cannotGetAttraction() throws Exception {
			Attraction attractionA = DataUtil.createAttractionA();
			attractionA.setAttractionId(0);

	        mockMvc.perform(
	                MockMvcRequestBuilders.get("/attraction/" + attractionA.getAttractionId())
	                    .contentType(MediaType.APPLICATION_JSON)
	        )
	        .andExpect(
	        		MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Attraction not found.")
			);
		}

		@Test
		public void cannotUpdateAttractionImproperName() throws Exception {
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
		public void cannotUpdateAttractionImproperDescription() throws Exception {
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
		public void cannotUpdateAttractionMissing() throws Exception {
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
				MockMvcResultMatchers.jsonPath("$.error").value("Attraction not found.")
			);
		}
		
		@Test
		public void cannotDeleteAttraction() throws Exception {
			int id = 1;
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/attraction/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Attraction with ID \""+ id + "\" not found.")
			);
		}
	 
 	}
}
