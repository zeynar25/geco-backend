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
import com.example.geco.domains.Account.Role;
import com.example.geco.dto.AccountResponse;
import com.example.geco.dto.AccountResponse.PasswordStatus;
import com.example.geco.dto.PasswordUpdateRequest;
import com.example.geco.dto.RoleUpdateRequest;
import com.example.geco.dto.SignupRequest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AccountControllerTests extends AbstractControllerTest {
	@Nested
    class SuccessTests {
		@Test
		public void shouldCreateTouristAccount() throws Exception {
			SignupRequest request = DataUtil.createSignupRequestA();
			String requestJson = objectMapper.writeValueAsString(request);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/account")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
			).andExpect(
					MockMvcResultMatchers.status().isCreated()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.passwordStatus").value(PasswordStatus.UNCHANGED.toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.email").value(request.getEmail())
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
		public void shouldReturnAllUsersByAdmin() throws Exception {
		    mockAdminAuthentication(1, "admin@email.com");
		    
		    SignupRequest requestA = DataUtil.createSignupRequestA();
		    requestA.setRole(Role.USER);
		    AccountResponse accountA = accountService.addAccountByAdmin(requestA);

		    SignupRequest requestB = DataUtil.createSignupRequestB();
		    requestB.setRole(Role.USER);
		    AccountResponse accountB = accountService.addAccountByAdmin(requestB);

		    mockMvc.perform(
		            MockMvcRequestBuilders.get("/account/staff/list/user")
		                    .param("page", "0")
		                    .param("size", "10")
		                    .contentType(MediaType.APPLICATION_JSON)
		    ).andExpect(
		            MockMvcResultMatchers.status().isOk())
		    
		    // Assert page metadata
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.number").value(0))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.size").value(10))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.totalElements").value(2))

		    // Assert list inside content[]
		    .andExpect(
					MockMvcResultMatchers.jsonPath("$.content[0].email").value(accountB.getEmail()))
		    .andExpect(
					MockMvcResultMatchers.jsonPath("$.content[0].passwordStatus").value(accountB.getPasswordStatus().toString()))
		    .andExpect(
					MockMvcResultMatchers.jsonPath("$.content[1].email").value(accountA.getEmail()))
		    .andExpect(
					MockMvcResultMatchers.jsonPath("$.content[1].passwordStatus").value(accountA.getPasswordStatus().toString()));
		}

		
		@Test
		@WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
		public void shouldReturnAllStaffsByAdmin() throws Exception {
		    mockAdminAuthentication(1, "admin@email.com");
		    
		    SignupRequest requestA = DataUtil.createSignupRequestA();
		    requestA.setRole(Role.STAFF);
		    AccountResponse accountA = accountService.addAccountByAdmin(requestA);
		
		    SignupRequest requestB = DataUtil.createSignupRequestB();
		    requestB.setRole(Role.STAFF);
		    AccountResponse accountB = accountService.addAccountByAdmin(requestB);
		
		    mockMvc.perform(
		            MockMvcRequestBuilders.get("/account/staff/list/staff")
		                    .param("page", "0")
		                    .param("size", "10")
		                    .contentType(MediaType.APPLICATION_JSON))
		    .andExpect(
		    	MockMvcResultMatchers.status().isOk())
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.number").value(0))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.size").value(10))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.totalElements").value(2))
		
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].email")
		    		.value(accountB.getEmail()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[0].passwordStatus")
		    		.value(accountB.getPasswordStatus().toString()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[1].email")
		    		.value(accountA.getEmail()))
		    .andExpect(
		    		MockMvcResultMatchers.jsonPath("$.content[1].passwordStatus")
		    		.value(accountA.getPasswordStatus().toString()));
		}
	
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
		public void shouldReturnAllAdminsByAdmin() throws Exception {
		    mockAdminAuthentication(1, "admin@email.com");

		    SignupRequest requestA = DataUtil.createSignupRequestA();
		    requestA.setRole(Role.ADMIN);
		    AccountResponse accountA = accountService.addAccountByAdmin(requestA);

		    SignupRequest requestB = DataUtil.createSignupRequestB();
		    requestB.setRole(Role.ADMIN);
		    AccountResponse accountB = accountService.addAccountByAdmin(requestB);

		    mockMvc.perform(
		            MockMvcRequestBuilders.get("/account/staff/list/admin")
		                    .param("page", "0")
		                    .param("size", "10")
		                    .contentType(MediaType.APPLICATION_JSON)
		    ).andExpect(
		    		MockMvcResultMatchers.status().isOk())
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.number").value(0))
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.size").value(10))
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.totalElements").value(3))

		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.content[0].email").value("admin@example.com"))
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.content[1].email").value(accountB.getEmail()))
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.content[2].email").value(accountA.getEmail()));
		}

		
		@Test
		@WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
		public void shouldReturnAllActiveUsersByAdmin() throws Exception {
		    mockAdminAuthentication(1, "admin@email.com");

		    SignupRequest requestA = DataUtil.createSignupRequestA();
		    requestA.setRole(Role.USER);
		    AccountResponse accountA = accountService.addAccountByAdmin(requestA);

		    SignupRequest requestB = DataUtil.createSignupRequestB();
		    requestB.setRole(Role.USER);
		    AccountResponse accountB = accountService.addAccountByAdmin(requestB);

		    accountService.softDeleteAccount(accountB.getAccountId());

		    mockMvc.perform(
		            MockMvcRequestBuilders.get("/account/staff/list/user/active")
		                    .param("page", "0")
		                    .param("size", "10")
		                    .contentType(MediaType.APPLICATION_JSON)
		    ).andExpect(
		    		MockMvcResultMatchers.status().isOk())
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.number").value(0))
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.size").value(10))
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.totalElements").value(1))

		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.content[0].email").value(accountA.getEmail()))
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.content[0].passwordStatus").value(accountA.getPasswordStatus().toString()));
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
		public void shouldReturnAllInactiveUsersByAdmin() throws Exception {
		    mockAdminAuthentication(1, "admin@email.com");

		    SignupRequest requestA = DataUtil.createSignupRequestA();
		    requestA.setRole(Role.USER);
		    accountService.addAccountByAdmin(requestA);

		    SignupRequest requestB = DataUtil.createSignupRequestB();
		    requestB.setRole(Role.USER);
		    AccountResponse inactiveAccount = accountService.addAccountByAdmin(requestB);

		    accountService.softDeleteAccount(inactiveAccount.getAccountId());

		    mockMvc.perform(
		            MockMvcRequestBuilders.get("/account/staff/list/user/inactive")
		                    .param("page", "0")
		                    .param("size", "10")
		                    .contentType(MediaType.APPLICATION_JSON)
		    ).andExpect(
		    		MockMvcResultMatchers.status().isOk())
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.number").value(0))
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.size").value(10))
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.totalElements").value(1))

		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.content[0].email").value(inactiveAccount.getEmail()))
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.content[0].passwordStatus").value(inactiveAccount.getPasswordStatus().toString()));
		}
		
		@Test
		@WithMockUser(username = "staff@email.com", roles = {"STAFF"})
		public void shouldReturnAllUsersByStaff() throws Exception {
		    mockStaffAuthentication(2, "staff@email.com");

		    SignupRequest requestA = DataUtil.createSignupRequestA();
		    AccountResponse accountA = accountService.addTouristAccount(requestA);

		    SignupRequest requestB = DataUtil.createSignupRequestB();
		    AccountResponse accountB = accountService.addTouristAccount(requestB);

		    mockMvc.perform(
		            MockMvcRequestBuilders.get("/account/staff/list/user")
		                    .param("page", "0")
		                    .param("size", "10")
		                    .contentType(MediaType.APPLICATION_JSON)
		    ).andExpect(
		    		MockMvcResultMatchers.status().isOk())
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.number").value(0))
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.size").value(10))
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.totalElements").value(2))

		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.content[0].email").value(accountB.getEmail()))
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.content[0].passwordStatus").value(accountB.getPasswordStatus().toString()))
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.content[1].email").value(accountA.getEmail()))
		     .andExpect(
		    		 MockMvcResultMatchers.jsonPath("$.content[1].passwordStatus").value(accountA.getPasswordStatus().toString()));
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
        public void shouldSoftDeleteAccount() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			SignupRequest requestA = DataUtil.createSignupRequestA();
			requestA.setRole(Role.USER);
			AccountResponse accountA = accountService.addAccountByAdmin(requestA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.delete("/account/admin/" + accountA.getAccountId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNoContent()
			);
        }
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
        public void shouldRestoreAccount() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			SignupRequest requestA = DataUtil.createSignupRequestA();
			requestA.setRole(Role.USER);
			AccountResponse accountA = accountService.addAccountByAdmin(requestA);
			
			accountService.softDeleteAccount(accountA.getAccountId());
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/account/admin/restore/" + accountA.getAccountId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isNoContent()
			);
        }
		
		@Test
		@WithMockUser(username = "staff@email.com", roles = {"STAFF"})
		public void shouldResetPasswordByStaff() throws Exception {
			mockStaffAuthentication(2, "staff@email.com");
			
			SignupRequest requestA = DataUtil.createSignupRequestA();
			AccountResponse accountA = accountService.addTouristAccount(requestA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/account/staff/reset-password/" + accountA.getAccountId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.passwordStatus").value(PasswordStatus.RESET_SUCCESSFULLY.toString())
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
		public void shouldResetPasswordByAdmin() throws Exception {
			mockStaffAuthentication(1, "admin@email.com");
			
			SignupRequest requestA = DataUtil.createSignupRequestA();
			AccountResponse accountA = accountService.addTouristAccount(requestA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/account/staff/reset-password/" + accountA.getAccountId())
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.passwordStatus").value(PasswordStatus.RESET_SUCCESSFULLY.toString())
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
		public void shouldUpdateAccountRoleToStaff() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			SignupRequest requestA = DataUtil.createSignupRequestA();
			AccountResponse accountA = accountService.addTouristAccount(requestA);
			
			RoleUpdateRequest updateRequest = RoleUpdateRequest.builder()
					.accountId(accountA.getAccountId())
					.role(Role.STAFF)
					.build();
			
			String requestJson = objectMapper.writeValueAsString(updateRequest);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/account/admin/update-role/" + accountA.getAccountId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.passwordStatus").value(PasswordStatus.UNCHANGED.toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.email").value(accountA.getEmail())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.role").value(updateRequest.getRole().toString())
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
		public void shouldUpdateAccountRoleToAdmin() throws Exception {
			mockAdminAuthentication(1, "admin@email.com");
			
			SignupRequest requestA = DataUtil.createSignupRequestA();
			AccountResponse accountA = accountService.addTouristAccount(requestA);
			
			RoleUpdateRequest updateRequest = RoleUpdateRequest.builder()
					.accountId(accountA.getAccountId())
					.role(Role.ADMIN)
					.build();
			
			String requestJson = objectMapper.writeValueAsString(updateRequest);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/account/admin/update-role/" + accountA.getAccountId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.passwordStatus").value(PasswordStatus.UNCHANGED.toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.email").value(accountA.getEmail())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.role").value(updateRequest.getRole().toString())
			);
		}
	}
	
	@Nested
	class FailureTests {
	    @Test
	    public void shouldFailToCreateAccountWithInvalidRequest() throws Exception {
	        // Missing required fields (email, password, etc)
	        SignupRequest request = new SignupRequest();
	        String requestJson = objectMapper.writeValueAsString(request);

	        mockMvc.perform(
	                MockMvcRequestBuilders.post("/account")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(requestJson)
	        ).andExpect(
	                MockMvcResultMatchers.status().isBadRequest()
	        );
	    }

	    @Test
	    public void shouldFailToCreateAccountWithDuplicateEmail() throws Exception {
	        SignupRequest request = DataUtil.createSignupRequestA();
	        accountService.addTouristAccount(request); // existing user

	        String json = objectMapper.writeValueAsString(request);

	        mockMvc.perform(
	                MockMvcRequestBuilders.post("/account")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	        ).andExpect(
	                MockMvcResultMatchers.status().isBadRequest()
	        );
	    }

	    @Test
	    public void shouldFailUnauthorizedAccessToStaffList() throws Exception {
	        // No role → should be unauthorized
	        mockMvc.perform(
	                MockMvcRequestBuilders.get("/account/staff/list/user")
	        ).andExpect(
	                MockMvcResultMatchers.status().isUnauthorized()
	        );
	    }

	    @Test
	    @WithMockUser(username = "user@email.com", roles = {"USER"})
	    public void shouldFailForbiddenIfUserTriesToAccessAdminEndpoints() throws Exception {
	        mockMvc.perform(
	                MockMvcRequestBuilders.get("/account/staff/list/admin")
	        ).andExpect(
	                MockMvcResultMatchers.status().isForbidden()
	        );
	    }

	    @Test
	    @WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
	    public void shouldFailToFetchNonExistingUser() throws Exception {
	        mockAdminAuthentication(1, "admin@email.com");

	        mockMvc.perform(
	                MockMvcRequestBuilders.get("/account/staff/list/user/999")
	        ).andExpect(
	                MockMvcResultMatchers.status().isNotFound()
	        );
	    }

	    @Test
	    @WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
	    public void shouldFailInvalidRoleUpdate() throws Exception {
	        mockAdminAuthentication(1, "admin@email.com");

	        SignupRequest request = DataUtil.createSignupRequestA();
	        request.setRole(Role.USER);
	        AccountResponse saved = accountService.addAccountByAdmin(request);

	        
	        RoleUpdateRequest roleRequest = RoleUpdateRequest.builder()
	        		.accountId(saved.getAccountId())
	        		.role(null) // invalid, no role assigned.
	        		.build();

	        String json = objectMapper.writeValueAsString(roleRequest);

	        mockMvc.perform(
	                MockMvcRequestBuilders.patch("/account/admin/update-role/" + saved.getAccountId())
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	        ).andExpect(
	                MockMvcResultMatchers.status().isBadRequest()
	        );
	    }

	    @Test
	    @WithMockUser(username = "user@email.com", roles = {"USER"})
	    public void shouldFailPasswordUpdateWithInvalidValues() throws Exception {
	        mockUserAuthentication(3, "user@email.com");
	        

	        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
	        		.oldPassword("wrongpass")
	        		.password("123") // invalid, too short
	        		.confirmPassword("123")
	        		.build();

	        String json = objectMapper.writeValueAsString(request);

	        mockMvc.perform(
	                MockMvcRequestBuilders.patch("/account/update-password/3")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(json)
	        ).andExpect(
	                MockMvcResultMatchers.status().isBadRequest()
	        );
	    }

	    @Test
	    @WithMockUser(username = "staff@email.com", roles = {"STAFF"})
	    public void shouldFailToResetPasswordOfNonExistingUser() throws Exception {
	        mockStaffAuthentication(2, "staff@email.com");

	        mockMvc.perform(
	                MockMvcRequestBuilders.patch("/account/staff/reset-password/999")
	        ).andExpect(
	                MockMvcResultMatchers.status().isNotFound()
	        );
	    }

	    @Test
	    @WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
	    public void shouldFailSoftDeleteOfNonExistingAccount() throws Exception {
	        mockAdminAuthentication(1, "admin@email.com");

	        mockMvc.perform(
	                MockMvcRequestBuilders.delete("/account/admin/999")
	        ).andExpect(
	                MockMvcResultMatchers.status().isNotFound()
	        );
	    }

	    @Test
	    @WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
	    public void shouldFailRestoreNonExistingAccount() throws Exception {
	        mockAdminAuthentication(1, "admin@email.com");

	        mockMvc.perform(
	                MockMvcRequestBuilders.patch("/account/admin/restore/999")
	        ).andExpect(
	                MockMvcResultMatchers.status().isNotFound()
	        );
	    }
	}
}
