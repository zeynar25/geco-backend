package com.example.geco.controllers;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.geco.DataUtil;
import com.example.geco.domains.PackageInclusion;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TourPackageControllerTests extends AbstractControllerTest {
	@Nested
    class SuccessTests {
		@Test
		public void canAddPackage() throws Exception{
//			List<PackageInclusion> inclusions = new ArrayList<>();
//			
//			TourPackage packageA = DataUtil.createPackageA();
//			String packageJson = objectMapper.writeValueAsString(packageA);
//			
//			mockMvc.perform(
//					MockMvcRequestBuilders.post("/package")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(packageA)
//			).andExpect(
//					MockMvcResultMatchers.status().isCreated()
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$.packageId").exists()
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$.description").value(packageA.getDescription())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$.basePrice").value(packageA.getBasePrice())
//			).andExpect(
//	        		MockMvcResultMatchers.jsonPath("$.inclusions").exists()
//			);
		}
	}
	
	@Nested
    class FailureTests {
		
	}
}