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
import com.example.geco.domains.TourPackage;
import com.example.geco.dto.TourPackageRequest;
import com.example.geco.dto.TourPackageUpdateRequest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TourPackageControllerTests extends AbstractControllerTest {
	@Nested
    class SuccessTests {
		@Test
		@WithMockUser(username = "staff@email.com", roles = "STAFF")
		public void canAddPackage() throws Exception{
			mockStaffAuthentication(2, "staff@email.com");
			
			TourPackageRequest requestA = DataUtil.createTourPackageRequestA(packageInclusionRepository);
			String packageJson = objectMapper.writeValueAsString(requestA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/package")
						.contentType(MediaType.APPLICATION_JSON)
						.content(packageJson)
			).andExpect(
					MockMvcResultMatchers.status().isCreated()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.packageId").exists()
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.name").value(requestA.getName())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.description").value(requestA.getDescription())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.duration").value(requestA.getDuration())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.basePrice").value(requestA.getBasePrice())
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.active").value("true")
			).andExpect(
	        		MockMvcResultMatchers.jsonPath("$.inclusions").exists()
			);
		}
		
		@Test
	    @WithMockUser(username = "staff@email.com", roles = "STAFF")
	    public void canGetPackage() throws Exception {
	        mockStaffAuthentication(2, "staff@email.com");

	        TourPackageRequest request = DataUtil.createTourPackageRequestA(packageInclusionRepository);
	        TourPackage savedPackage = tourPackageService.addPackage(request);

	        mockMvc.perform(
	                MockMvcRequestBuilders.get("/package/" + savedPackage.getPackageId())
	                        .contentType(MediaType.APPLICATION_JSON)
	        ).andExpect(MockMvcResultMatchers.status().isOk())
	         .andExpect(MockMvcResultMatchers.jsonPath("$.packageId").value(savedPackage.getPackageId()))
	         .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(savedPackage.getName()))
	         .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(savedPackage.getDescription()))
	         .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(savedPackage.getDuration()))
	         .andExpect(MockMvcResultMatchers.jsonPath("$.basePrice").value(savedPackage.getBasePrice()))
	         .andExpect(MockMvcResultMatchers.jsonPath("$.inclusions").exists());
	    }

	    @Test
	    @WithMockUser(username = "staff@email.com", roles = "STAFF")
	    public void canGetAllPackages() throws Exception {
	        mockStaffAuthentication(2, "staff@email.com");

	        TourPackageRequest requestA = DataUtil.createTourPackageRequestA(packageInclusionRepository);
	        TourPackage savedA = tourPackageService.addPackage(requestA);

	        TourPackageRequest requestB = DataUtil.createTourPackageRequestB(packageInclusionRepository);
	        TourPackage savedB = tourPackageService.addPackage(requestB);

	        mockMvc.perform(
	                MockMvcRequestBuilders.get("/package")
	                        .contentType(MediaType.APPLICATION_JSON)
	        ).andExpect(MockMvcResultMatchers.status().isOk())
	         .andExpect(MockMvcResultMatchers.jsonPath("$[0].packageId").value(savedA.getPackageId()))
	         .andExpect(MockMvcResultMatchers.jsonPath("$[1].packageId").value(savedB.getPackageId()));
	    }

	    @Test
	    @WithMockUser(username = "staff@email.com", roles = "STAFF")
	    public void canUpdatePackage() throws Exception {
	        mockStaffAuthentication(2, "staff@email.com");

	        TourPackageRequest request = DataUtil.createTourPackageRequestA(packageInclusionRepository);
	        TourPackage savedPackage = tourPackageService.addPackage(request);

	        TourPackageUpdateRequest updateRequest = new TourPackageUpdateRequest();
	        updateRequest.setDescription("Updated description");
	        updateRequest.setBasePrice(999.0);
	        String updateJson = objectMapper.writeValueAsString(updateRequest);

	        mockMvc.perform(
	                MockMvcRequestBuilders.patch("/package/" + savedPackage.getPackageId())
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(updateJson)
	        ).andExpect(MockMvcResultMatchers.status().isOk())
	         .andExpect(MockMvcResultMatchers.jsonPath("$.packageId").value(savedPackage.getPackageId()))
	         .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(updateRequest.getDescription()))
	         .andExpect(MockMvcResultMatchers.jsonPath("$.basePrice").value(updateRequest.getBasePrice()));
	    }

	    @Test
	    @WithMockUser(username = "admin@email.com", roles = "ADMIN")
	    public void canSoftDeleteAndRestorePackage() throws Exception {
	        mockAdminAuthentication(1, "admin@email.com");

	        TourPackageRequest request = DataUtil.createTourPackageRequestA(packageInclusionRepository);
	        TourPackage savedPackage = tourPackageService.addPackage(request);

	        // Soft delete
	        mockMvc.perform(
	                MockMvcRequestBuilders.delete("/package/" + savedPackage.getPackageId())
	                        .param("soft", "true")
	        ).andExpect(MockMvcResultMatchers.status().isNoContent());

	        // Package should be inactive
	        TourPackage deletedPackage = tourPackageService.getPackage(savedPackage.getPackageId());
	        assert !deletedPackage.isActive();

	        // Restore
	        mockMvc.perform(
	                MockMvcRequestBuilders.patch("/package/admin/restore/" + savedPackage.getPackageId())
	        ).andExpect(MockMvcResultMatchers.status().isNoContent());

	        // Package should be active again
	        TourPackage restoredPackage = tourPackageService.getPackage(savedPackage.getPackageId());
	        assert restoredPackage.isActive();
	    }

	    @Test
	    @WithMockUser(username = "admin@email.com", roles = "ADMIN")
	    public void canHardDeletePackage() throws Exception {
	        mockAdminAuthentication(1, "admin@email.com");

	        TourPackageRequest request = DataUtil.createTourPackageRequestA(packageInclusionRepository);
	        TourPackage savedPackage = tourPackageService.addPackage(request);

	        mockMvc.perform(
	                MockMvcRequestBuilders.delete("/package/" + savedPackage.getPackageId())
	                        .param("soft", "false")
	        ).andExpect(MockMvcResultMatchers.status().isNoContent());

	       
	        mockMvc.perform(
	                MockMvcRequestBuilders.get("/package/" + savedPackage.getPackageId())
	        ).andExpect(MockMvcResultMatchers.status().isNotFound());
	    }
	    
	    @Test
	    @WithMockUser(username = "admin@email.com", roles = "ADMIN")
	    public void canRestoreSoftDeletedPackage() throws Exception {
	        mockAdminAuthentication(1, "admin@email.com");

	        TourPackageRequest request = DataUtil.createTourPackageRequestA(packageInclusionRepository);
	        TourPackage savedPackage = tourPackageService.addPackage(request);

	        mockMvc.perform(
	                MockMvcRequestBuilders.delete("/package/" + savedPackage.getPackageId())
	                        .param("soft", "true")
	        ).andExpect(MockMvcResultMatchers.status().isNoContent());

	        TourPackage softDeleted = tourPackageService.getPackage(savedPackage.getPackageId());
	        assert !softDeleted.isActive();

	        mockMvc.perform(
	                MockMvcRequestBuilders.patch("/package/admin/restore/" + savedPackage.getPackageId())
	        ).andExpect(MockMvcResultMatchers.status().isNoContent());

	        // 4. Verify package is active again
	        TourPackage restoredPackage = tourPackageService.getPackage(savedPackage.getPackageId());
	        assert restoredPackage.isActive();
	    }
	}
	
	@Nested
    class FailureTests {
	    @Test
	    @WithMockUser(username = "staff@email.com", roles = "STAFF")
	    public void cannotAddPackageNullDescription() throws Exception {
	        mockStaffAuthentication(2, "staff@email.com");

	        TourPackageRequest request = DataUtil.createTourPackageRequestA(packageInclusionRepository);
	        request.setDescription(null);
	        String json = objectMapper.writeValueAsString(request);

	        mockMvc.perform(
	                MockMvcRequestBuilders.post("/package")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	        ).andExpect(MockMvcResultMatchers.status().isBadRequest())
	         .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Description is required."));
	    }

	    @Test
	    @WithMockUser(username = "user@email.com", roles = "USER")
	    public void userCannotAccessStaffEndpoint() throws Exception {
	        mockUserAuthentication(3, "user@email.com");

	        TourPackageRequest request = DataUtil.createTourPackageRequestA(packageInclusionRepository);
	        String json = objectMapper.writeValueAsString(request);

	        mockMvc.perform(
	                MockMvcRequestBuilders.post("/package")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	        ).andExpect(MockMvcResultMatchers.status().isForbidden());
	    }

	    @Test
	    @WithMockUser(username = "user@email.com", roles = "USER")
	    public void userCannotAccessAdminEndpoint() throws Exception {
	        mockUserAuthentication(3, "user@email.com");

	        int packageId = 1; // any ID
	        mockMvc.perform(
	                MockMvcRequestBuilders.delete("/package/" + packageId)
	                        .param("soft", "true")
	        ).andExpect(MockMvcResultMatchers.status().isForbidden());
	    }

	    @Test
	    @WithMockUser(username = "staff@email.com", roles = "STAFF")
	    public void staffCannotAccessAdminOnlyEndpoint() throws Exception {
	        mockStaffAuthentication(2, "staff@email.com");

	        int packageId = 1; // any ID
	        mockMvc.perform(
	                MockMvcRequestBuilders.delete("/package/" + packageId)
	                        .param("soft", "true")
	        ).andExpect(MockMvcResultMatchers.status().isForbidden());
	    }

	    @Test
	    @WithMockUser(username = "user@email.com", roles = "USER")
	    public void userCannotGetOtherProtectedStaffResources() throws Exception {
	        mockUserAuthentication(3, "user@email.com");

	        mockMvc.perform(
	                MockMvcRequestBuilders.get("/package")
	                        .contentType(MediaType.APPLICATION_JSON)
	        ).andExpect(MockMvcResultMatchers.status().isForbidden());
	    }

	    @Test
	    @WithMockUser(username = "staff@email.com", roles = "STAFF")
	    public void cannotGetNonExistingPackage() throws Exception {
	        mockStaffAuthentication(2, "staff@email.com");

	        int nonExistentId = 999;

	        mockMvc.perform(
	                MockMvcRequestBuilders.get("/package/" + nonExistentId)
	                        .contentType(MediaType.APPLICATION_JSON)
	        ).andExpect(MockMvcResultMatchers.status().isNotFound())
	         .andExpect(MockMvcResultMatchers.jsonPath("$.error")
	                 .value("Tour package with ID '" + nonExistentId + "' not found."));
	    }

	    @Test
	    @WithMockUser(username = "staff@email.com", roles = "STAFF")
	    public void cannotUpdateNonExistingPackage() throws Exception {
	        mockStaffAuthentication(2, "staff@email.com");

	        int nonExistentId = 999;
	        TourPackageUpdateRequest updateRequest = new TourPackageUpdateRequest();
	        updateRequest.setDescription("Updated Description");
	        updateRequest.setBasePrice(123.0);
	        String json = objectMapper.writeValueAsString(updateRequest);

	        mockMvc.perform(
	                MockMvcRequestBuilders.patch("/package/" + nonExistentId)
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	        ).andExpect(MockMvcResultMatchers.status().isNotFound())
	         .andExpect(MockMvcResultMatchers.jsonPath("$.error")
	                 .value("Tour package with ID '" + nonExistentId + "' not found."));
	    }

	    @Test
	    @WithMockUser(username = "admin@email.com", roles = "ADMIN")
	    public void cannotDeleteNonExistingPackage() throws Exception {
	        mockAdminAuthentication(1, "admin@email.com");

	        int nonExistentId = 999;

	        mockMvc.perform(
	                MockMvcRequestBuilders.delete("/package/" + nonExistentId)
	                        .param("soft", "true")
	        ).andExpect(
	        		MockMvcResultMatchers.status().isNotFound()
	        ).andExpect(
	        		 MockMvcResultMatchers.jsonPath("$.error")
	        		 .value("Tour package with ID '" + nonExistentId + "' not found.")
	        );
	    }
	}
}