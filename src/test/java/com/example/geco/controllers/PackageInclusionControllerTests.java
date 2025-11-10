package com.example.geco.controllers;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.geco.DataUtil;
import com.example.geco.domains.PackageInclusion;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PackageInclusionControllerTests extends AbstractControllerTest{
	@Nested
    class SuccessTests {
		@Test
		public void canAddInclusion() throws Exception{
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			String inclusionJson = objectMapper.writeValueAsString(inclusionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/package-inclusion")
						.contentType(MediaType.APPLICATION_JSON)
						.content(inclusionJson)
			).andExpect(
					MockMvcResultMatchers.status().isCreated()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionId").exists()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionName").value(inclusionA.getInclusionName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionPricePerPerson").value(inclusionA.getInclusionPricePerPerson())
			);
		}
		
		@Test
		public void canGetInclusion() throws Exception {
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			PackageInclusion savedInclusionA = packageInclusionService.addInclusion(inclusionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/package-inclusion/" + savedInclusionA.getInclusionId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionId").value(savedInclusionA.getInclusionId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionName").value(savedInclusionA.getInclusionName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionPricePerPerson").value(savedInclusionA.getInclusionPricePerPerson())
			);
		}
		
		@Test
		public void canGetAllInclusions() throws Exception {
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			PackageInclusion savedInclusionA = packageInclusionService.addInclusion(inclusionA);

			PackageInclusion inclusionB = DataUtil.createPackageInclusionB();
			PackageInclusion savedInclusionB = packageInclusionService.addInclusion(inclusionB);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/package-inclusion")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].inclusionId").value(savedInclusionA.getInclusionId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].inclusionName").value(savedInclusionA.getInclusionName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].inclusionPricePerPerson").value(savedInclusionA.getInclusionPricePerPerson())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].inclusionId").value(savedInclusionB.getInclusionId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].inclusionName").value(savedInclusionB.getInclusionName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].inclusionPricePerPerson").value(savedInclusionB.getInclusionPricePerPerson())
			);
		}
		
		@Test
		public void canGetAllInclusionsEmpty() throws Exception {
			mockMvc.perform(
					MockMvcRequestBuilders.get("/package-inclusion")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$").isEmpty()
			);
		}
		
		@Test
		public void canUpdateInclusion() throws Exception {
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			PackageInclusion savedInclusionA = packageInclusionService.addInclusion(inclusionA);
			
			inclusionA.setInclusionName("New inclusion name for this package inclusion.");
			inclusionA.setInclusionPricePerPerson(200);
			String inclusionJson = objectMapper.writeValueAsString(inclusionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/package-inclusion/" + savedInclusionA.getInclusionId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(inclusionJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionId").value(savedInclusionA.getInclusionId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionName").value(inclusionA.getInclusionName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionPricePerPerson").value(inclusionA.getInclusionPricePerPerson())
			);
		}
		
		@Test
		public void canUpdateInclusionNameOnly() throws Exception {
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			PackageInclusion savedInclusionA = packageInclusionService.addInclusion(inclusionA);
			

			PackageInclusion newInclusion = new PackageInclusion();
			newInclusion.setInclusionName("New inclusion name for this package inclusion.");
			newInclusion.setInclusionPricePerPerson(null);
		    
			String inclusionJson = objectMapper.writeValueAsString(newInclusion);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/package-inclusion/" + savedInclusionA.getInclusionId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(inclusionJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionId").value(savedInclusionA.getInclusionId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionName").value(newInclusion.getInclusionName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionPricePerPerson").value(savedInclusionA.getInclusionPricePerPerson())
			);
		}
		
		@Test
		public void canUpdateInclusionPriceOnly() throws Exception {
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			PackageInclusion savedInclusionA = packageInclusionService.addInclusion(inclusionA);
			
			PackageInclusion newInclusion = new PackageInclusion();
			newInclusion.setInclusionName(null);
			newInclusion.setInclusionPricePerPerson(200);
		    
			String inclusionJson = objectMapper.writeValueAsString(newInclusion);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/package-inclusion/" + savedInclusionA.getInclusionId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(inclusionJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionId").value(savedInclusionA.getInclusionId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionName").value(savedInclusionA.getInclusionName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionPricePerPerson").value(newInclusion.getInclusionPricePerPerson())
			);
		}

		@Test
		public void canDeleteFaq() throws Exception {
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			PackageInclusion savedInclusionA = packageInclusionService.addInclusion(inclusionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/package-inclusion/" + savedInclusionA.getInclusionId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionId").value(savedInclusionA.getInclusionId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionName").value(savedInclusionA.getInclusionName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusionPricePerPerson").value(savedInclusionA.getInclusionPricePerPerson())
			);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/package-inclusion/" + savedInclusionA.getInclusionId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			);
		}
	}
	
	@Nested
    class FailureTests {
		@Test
		public void cannotAddInclusionNullName() throws Exception{
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			inclusionA.setInclusionName(null);
			String inclusionJson = objectMapper.writeValueAsString(inclusionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/package-inclusion")
						.contentType(MediaType.APPLICATION_JSON)
						.content(inclusionJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Inclusion name is missing.")
			);
		}

		@Test
		public void cannotAddInclusionEmptyName() throws Exception{
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			inclusionA.setInclusionName("   ");
			String inclusionJson = objectMapper.writeValueAsString(inclusionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/package-inclusion")
						.contentType(MediaType.APPLICATION_JSON)
						.content(inclusionJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Inclusion name is missing.")
			);
		}		

		@Test
		public void cannotAddInclusionNullPrice() throws Exception{
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			inclusionA.setInclusionPricePerPerson(null);
			String inclusionJson = objectMapper.writeValueAsString(inclusionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/package-inclusion")
						.contentType(MediaType.APPLICATION_JSON)
						.content(inclusionJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Inclusion price per person is missing.")
			);
		}
		@Test
		public void cannotAddInclusionInvalidPrice() throws Exception{
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			inclusionA.setInclusionPricePerPerson(-1);
			String inclusionJson = objectMapper.writeValueAsString(inclusionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/package-inclusion")
						.contentType(MediaType.APPLICATION_JSON)
						.content(inclusionJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Invalid Inclusion price per person.")
			);
		}
		
		@Test
		public void cannotGetInclusion() throws Exception {
			int id = 0;
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/package-inclusion/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Package Inclusion with ID \"" + id + "\" not found.")
			);
		}

		@Test
		public void cannotUpdateInclusionNameAndPriceMissing() throws Exception {
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			PackageInclusion savedInclusionA = packageInclusionService.addInclusion(inclusionA);
			
			inclusionA.setInclusionName(null);
			inclusionA.setInclusionPricePerPerson(null);
			String inclusionJson = objectMapper.writeValueAsString(inclusionA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/package-inclusion/" + savedInclusionA.getInclusionId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(inclusionJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Package Inclusion name and price per person are missing.")
			);
		}

		@Test
		public void cannotUpdateInclusionIdNotFound() throws Exception {
			PackageInclusion inclusionA = DataUtil.createPackageInclusionA();
			String inclusionJson = objectMapper.writeValueAsString(inclusionA);

			int id = 0;
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/package-inclusion/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(inclusionJson)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Package Inclusion with ID \"" + id + "\" not found.")
			);
		}

		@Test
		public void canDeleteFaq() throws Exception {
			int id = 0;
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/package-inclusion/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
    			MockMvcResultMatchers.jsonPath("$.error").value("Package Inclusion with ID \"" + id + "\" not found.")
    		);
		}
	}
}
