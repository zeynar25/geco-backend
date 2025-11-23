package com.example.geco.controllers;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.geco.AbstractControllerTest;
import com.example.geco.DataUtil;
import com.example.geco.domains.Account;
import com.example.geco.domains.Account.Role;
import com.example.geco.domains.UserDetail;
import com.example.geco.dto.AccountResponse;
import com.example.geco.dto.AccountResponse.PasswordStatus;
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
        public void shouldReturnAllGuestsByAdmin() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			SignupRequest requestA = DataUtil.createSignupRequestA();
			requestA.setRole(Role.GUEST);
			AccountResponse accountA = accountService.addAccountByAdmin(requestA);

			SignupRequest requestB = DataUtil.createSignupRequestB();
			requestB.setRole(Role.GUEST);
			AccountResponse accountB = accountService.addAccountByAdmin(requestB);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/account/staff/list/guest")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].email").value(accountB.getEmail())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].passwordStatus").value(accountB.getPasswordStatus().toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].email").value(accountA.getEmail())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].passwordStatus").value(accountA.getPasswordStatus().toString())
			);
        }
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
        public void shouldReturnAllStaffsByAdmin() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			SignupRequest requestA = DataUtil.createSignupRequestA();
			requestA.setRole(Role.STAFF);
			AccountResponse accountA = accountService.addAccountByAdmin(requestA);

			SignupRequest requestB = DataUtil.createSignupRequestB();
			requestB.setRole(Role.STAFF);
			AccountResponse accountB = accountService.addAccountByAdmin(requestB);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/account/staff/list/staff")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].email").value(accountB.getEmail())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].passwordStatus").value(accountB.getPasswordStatus().toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].email").value(accountA.getEmail())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].passwordStatus").value(accountA.getPasswordStatus().toString())
			);
        }	
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
        public void shouldReturnAllAdminsByAdmin() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			SignupRequest requestA = DataUtil.createSignupRequestA();
			requestA.setRole(Role.ADMIN);
			AccountResponse accountA = accountService.addAccountByAdmin(requestA);

			SignupRequest requestB = DataUtil.createSignupRequestB();
			requestB.setRole(Role.ADMIN);
			AccountResponse accountB = accountService.addAccountByAdmin(requestB);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/account/staff/list/admin")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].email").value("admin@example.com")
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].passwordStatus").value(accountB.getPasswordStatus().toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].email").value(accountB.getEmail())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].passwordStatus").value(accountB.getPasswordStatus().toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[2].email").value(accountA.getEmail())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[2].passwordStatus").value(accountA.getPasswordStatus().toString())
			);
        }	
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
        public void shouldReturnAllActiveGuestsByAdmin() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			SignupRequest requestA = DataUtil.createSignupRequestA();
			requestA.setRole(Role.GUEST);
			AccountResponse accountA = accountService.addAccountByAdmin(requestA);

			SignupRequest requestB = DataUtil.createSignupRequestB();
			requestB.setRole(Role.GUEST);
			AccountResponse accountB = accountService.addAccountByAdmin(requestB);
			
			accountService.softDeleteAccount(accountB.getAccountId());
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/account/staff/list/guest/active")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].email").value(accountA.getEmail())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].passwordStatus").value(accountA.getPasswordStatus().toString())
			);
        }
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
        public void shouldReturnAllInactiveGuestsByAdmin() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			SignupRequest requestA = DataUtil.createSignupRequestA();
			requestA.setRole(Role.GUEST);
			AccountResponse accountA = accountService.addAccountByAdmin(requestA);

			SignupRequest requestB = DataUtil.createSignupRequestB();
			requestB.setRole(Role.GUEST);
			AccountResponse accountB = accountService.addAccountByAdmin(requestB);
			
			accountService.softDeleteAccount(accountB.getAccountId());
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/account/staff/list/guest/inactive")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].email").value(accountB.getEmail())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].passwordStatus").value(accountB.getPasswordStatus().toString())
			);
        }
		
		@Test
		@WithMockUser(username = "staff@email.com", roles = {"STAFF"})
        public void shouldReturnAllGuestsByStaff() throws Exception {
			mockStaffAuthentication("staff@email.com");
			
			SignupRequest requestA = DataUtil.createSignupRequestA();
			AccountResponse accountA = accountService.addTouristAccount(requestA);

			SignupRequest requestB = DataUtil.createSignupRequestB();
			AccountResponse accountB = accountService.addTouristAccount(requestB);
			
			mockMvc.perform(
					MockMvcRequestBuilders.get("/account/staff/list/guest")
						.contentType(MediaType.APPLICATION_JSON)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].email").value(accountB.getEmail())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[0].passwordStatus").value(accountB.getPasswordStatus().toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].email").value(accountA.getEmail())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$[1].passwordStatus").value(accountA.getPasswordStatus().toString())
			);
        }
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
        public void shouldSoftDeleteAccount() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			SignupRequest requestA = DataUtil.createSignupRequestA();
			requestA.setRole(Role.GUEST);
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
			mockAdminAuthentication("admin@email.com");
			
			SignupRequest requestA = DataUtil.createSignupRequestA();
			requestA.setRole(Role.GUEST);
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
			mockStaffAuthentication("staff@email.com");
			
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
			mockStaffAuthentication("admin@email.com");
			
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
			mockAdminAuthentication("admin@email.com");
			
			SignupRequest requestA = DataUtil.createSignupRequestA();
			AccountResponse accountA = accountService.addTouristAccount(requestA);
			
			requestA.setRole(Role.STAFF);
			String requestJson = objectMapper.writeValueAsString(requestA);
			
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
					MockMvcResultMatchers.jsonPath("$.role").value(requestA.getRole().toString())
			);
		}
		
		@Test
		@WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
		public void shouldUpdateAccountRoleToAdmin() throws Exception {
			mockAdminAuthentication("admin@email.com");
			
			SignupRequest requestA = DataUtil.createSignupRequestA();
			AccountResponse accountA = accountService.addTouristAccount(requestA);
			
			requestA.setRole(Role.ADMIN);
			String requestJson = objectMapper.writeValueAsString(requestA);
			
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
					MockMvcResultMatchers.jsonPath("$.role").value(requestA.getRole().toString())
			);
		}
	}
	
	@Nested
    class FailureTests {
	}
}
