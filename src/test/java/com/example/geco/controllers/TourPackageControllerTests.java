package com.example.geco.controllers;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.geco.AbstractControllerTest;
import com.example.geco.DataUtil;
import com.example.geco.domains.TourPackage;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TourPackageControllerTests extends AbstractControllerTest {
	@Nested
    class SuccessTests {
		@Test
		public void canAddPackage() throws Exception{
			TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
			String packageJson = objectMapper.writeValueAsString(packageA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/package")
						.contentType(MediaType.APPLICATION_JSON)
						.content(packageJson)
			).andExpect(
					MockMvcResultMatchers.status().isCreated()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.packageId").exists()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.name").value(packageA.getName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.description").value(packageA.getDescription())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.basePrice").value(packageA.getBasePrice())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusions").exists()
			);
		}
		
		@Test
		public void canGetPackage() throws Exception {
			TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/package/" + savedPackageA.getPackageId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.packageId").value(savedPackageA.getPackageId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.name").value(savedPackageA.getName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.description").value(savedPackageA.getDescription())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.basePrice").value(savedPackageA.getBasePrice())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusions").exists()
			);
		}
		
		@Test
		public void canGetAllPackages() throws Exception {
			TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			TourPackage packageB = DataUtil.createPackageB(packageInclusionService);
			TourPackage savedPackageB = tourPackageService.addPackage(packageB);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/package")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].packageId").value(savedPackageA.getPackageId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].name").value(savedPackageA.getName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].description").value(savedPackageA.getDescription())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].basePrice").value(savedPackageA.getBasePrice())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[0].inclusions").exists()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].packageId").value(savedPackageB.getPackageId())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].name").value(savedPackageB.getName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].description").value(savedPackageB.getDescription())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].basePrice").value(savedPackageB.getBasePrice())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$[1].inclusions").exists()
			);
		}
		
		@Test
		public void canGetAllPackagesEmpty() throws Exception {
			mockMvc.perform(
					MockMvcRequestBuilders.get("/package")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$").isEmpty()
			);
		}
		
		@Test
		public void canUpdatePackage() throws Exception{
			TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			packageA.setDescription("new description for this package");
			packageA.setBasePrice(100);
			String packageJson = objectMapper.writeValueAsString(packageA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/package/" + savedPackageA.getPackageId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(packageJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.packageId").exists()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.name").value(savedPackageA.getName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.description").value(packageA.getDescription())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.basePrice").value(packageA.getBasePrice())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusions").exists()
			);
		}

		@Test
		public void canUpdatePackageDescriptionOnly() throws Exception{
			TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);

			TourPackage newPackage = new TourPackage(
					savedPackageA.getPackageId(),
					"The light at the end of the tunnel",
					120,
					"new description for this package",
					null,
					null
			);
			
			String packageJson = objectMapper.writeValueAsString(newPackage);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/package/" + savedPackageA.getPackageId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(packageJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.packageId").exists()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.name").value(newPackage.getName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.description").value(newPackage.getDescription())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.basePrice").value(savedPackageA.getBasePrice())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusions").exists()
			);
		}

		@Test
		public void canUpdatePackagePriceOnly() throws Exception{
			TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);

			TourPackage newPackage = new TourPackage(
					savedPackageA.getPackageId(),
					null,
					60,
					null,
					100,
					null
			);
			
			String packageJson = objectMapper.writeValueAsString(newPackage);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/package/" + savedPackageA.getPackageId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(packageJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.packageId").exists()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.name").value(savedPackageA.getName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.description").value(savedPackageA.getDescription())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.basePrice").value(newPackage.getBasePrice())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusions").exists()
			);
		}

		@Test
		public void canDeletePackage() throws Exception {
			TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
			TourPackage savedPackageA = tourPackageService.addPackage(packageA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/package/" + savedPackageA.getPackageId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNoContent()
			);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/package/" + savedPackageA.getPackageId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			);
		}
	}
	
	@Nested
    class FailureTests {
		@Test
		public void cannotAddPackageNullDescription() throws Exception{
			TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
			packageA.setDescription(null);
			String packageJson = objectMapper.writeValueAsString(packageA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/package")
						.contentType(MediaType.APPLICATION_JSON)
						.content(packageJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Description is missing.")
			);
		}
		
		@Test
		public void cannotAddPackageBlankDescription() throws Exception{
			TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
			packageA.setDescription("    ");
			String packageJson = objectMapper.writeValueAsString(packageA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/package")
						.contentType(MediaType.APPLICATION_JSON)
						.content(packageJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Description is missing.")
			);
		}
		
		@Test
		public void cannotAddPackageShortDescription() throws Exception{
			TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
			packageA.setDescription("short");
			String packageJson = objectMapper.writeValueAsString(packageA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/package")
						.contentType(MediaType.APPLICATION_JSON)
						.content(packageJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Description must have at least 10 characters.")
			);
		}
		
		@Test
		public void cannotAddPackageNullPrice() throws Exception{
			TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
			packageA.setBasePrice(null);
			String packageJson = objectMapper.writeValueAsString(packageA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/package")
						.contentType(MediaType.APPLICATION_JSON)
						.content(packageJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Base price for the package is missing.")
			);
		}
		
		@Test
		public void cannotAddPackageInvalidPrice() throws Exception{
			TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
			packageA.setBasePrice(-1);
			String packageJson = objectMapper.writeValueAsString(packageA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/package")
						.contentType(MediaType.APPLICATION_JSON)
						.content(packageJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Invalid Base Price for the package.")
			);
		}
		
		@Test
		public void cannotAddPackageNullInclusion() throws Exception{
			TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
			packageA.setInclusions(null);
			String packageJson = objectMapper.writeValueAsString(packageA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/package")
						.contentType(MediaType.APPLICATION_JSON)
						.content(packageJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Package Inclusions are missing.")
			);
		}
		
		@Test
		public void cannotAddPackageMissingInclusion() throws Exception{
			TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
			packageA.getInclusions().clear();
			String packageJson = objectMapper.writeValueAsString(packageA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/package")
						.contentType(MediaType.APPLICATION_JSON)
						.content(packageJson)
			).andExpect(
					MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Package Inclusions are missing.")
			);
		}
		
		@Test
		public void cannotUpdatePackageAllMissing() throws Exception {
			TourPackage packageA = new TourPackage();
			packageA.setPackageId(0);
			String packageJson = objectMapper.writeValueAsString(packageA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/package/" + packageA.getPackageId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(packageJson)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Package with ID \"" + packageA.getPackageId() + "\" not found.")
			);
		}
		
		@Test
		public void cannotUpdatePackageIdNotFound() throws Exception {
			TourPackage packageA = DataUtil.createPackageA(packageInclusionService);
			packageA.setPackageId(0);
			String packageJson = objectMapper.writeValueAsString(packageA);

			mockMvc.perform(
					MockMvcRequestBuilders.patch("/package/" + packageA.getPackageId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(packageJson)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Package with ID \"" + packageA.getPackageId() + "\" not found.")
			);
		}
		
		@Test
		public void cannotGetPackage() throws Exception {
			int id = 0;

			mockMvc.perform(
					MockMvcRequestBuilders.get("/package/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Package with ID \"" + id + "\" not found.")
			);
		}

		@Test
		public void cannotDeletePackage() throws Exception {
			int id = 0;

			mockMvc.perform(
					MockMvcRequestBuilders.delete("/package/" + id)
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNotFound()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.error").value("Package with ID \"" + id + "\" not found.")
			);
		}
	}
}