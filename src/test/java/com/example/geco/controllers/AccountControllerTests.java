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
import com.example.geco.domains.Account;
import com.example.geco.domains.UserDetail;
import com.example.geco.dto.AccountResponse;
import com.example.geco.dto.DetailRequest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AccountControllerTests extends AbstractControllerTest {
	@Nested
    class SuccessTests {
		@Test
		public void canAddAccount() throws Exception {
			Account accountA = DataUtil.createAccountA();
			String accountJson = objectMapper.writeValueAsString(accountA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.post("/account")
						.contentType(MediaType.APPLICATION_JSON)
						.content(accountJson)
			).andExpect(
					MockMvcResultMatchers.status().isCreated()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.passwordNotice").exists()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.surname").value(accountA.getDetail().getSurname())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.firstName").value(accountA.getDetail().getFirstName())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.email").value(accountA.getDetail().getEmail())
			);
		}
		
		@Test
		public void canUpdateAccountPassword() throws Exception {
			Account accountA = DataUtil.createAccountA();
			
			// Save a guest account in the database.
			AccountResponse savedResponse = accountService.addTouristAccount(accountA);
			
			// Fetch the saved account and detail
		    Account managedAccount = accountRepository.findById(savedResponse.getAccountId()).get();
		    UserDetail managedDetail = managedAccount.getDetail();
		   
		    String newPassword = "newstrongpassword";
		    String censoredPassword = "*".repeat(newPassword.length());
		    managedAccount.setPassword(newPassword);
			
		    String accountJson = objectMapper.writeValueAsString(managedAccount);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/account/update-account/" + managedAccount.getAccountId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(accountJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.passwordNotice").value(censoredPassword)
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.role").value(managedAccount.getRole().toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.surname").value(managedDetail.getSurname())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.firstName").value(managedDetail.getFirstName())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.email").value(managedDetail.getEmail())
			);
		}
		
		@Test
		public void canUpdateAccountDetailsEmail() throws Exception {
			Account accountA = DataUtil.createAccountA();
			AccountResponse savedResponse = accountService.addTouristAccount(accountA);
			
			// Fetch the saved account and detail
		    Account managedAccount = accountRepository.findById(savedResponse.getAccountId()).get();
		    UserDetail managedDetail = managedAccount.getDetail();
		    
		    DetailRequest newRequest = new DetailRequest();
		    newRequest.setAccountId(managedAccount.getAccountId());
		    newRequest.setEmail("new@gmail.com");
		    
		    String requestJson = objectMapper.writeValueAsString(newRequest);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/account/update-details/" + managedAccount.getAccountId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
			).andExpect(
					MockMvcResultMatchers.status().isOk()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.passwordNotice").value("No changes made")
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.role").value(managedAccount.getRole().toString())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.surname").value(managedDetail.getSurname())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.firstName").value(managedDetail.getFirstName())
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.email").value(newRequest.getEmail())
			);
		}
	}
	
	@Nested
    class FailureTests {
		@Test
		public void cannotAddAccountImproperPassword() throws Exception {
			Account accountA = DataUtil.createAccountA();
			accountA.setPassword("123"); // too short for a password.
		    String accountJson = objectMapper.writeValueAsString(accountA);

		    mockMvc.perform(
		            MockMvcRequestBuilders.post("/account")
		                    .contentType(MediaType.APPLICATION_JSON)
		                    .content(accountJson)
		    ).andExpect(
		    		MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Password must have at least 8 characters.")
			);
		}
		
		@Test
		public void cannotUpdateAccountPasswordNotFound() throws Exception {
			Account accountA = DataUtil.createAccountA();
			accountA.setAccountId(1);
			// Did not save request through accountService.
			
			accountA.setPassword("123321123321");
		    String requestJson = objectMapper.writeValueAsString(accountA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/account/update-account/" + accountA.getAccountId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson)
			).andExpect(
		    		MockMvcResultMatchers.status().isNotFound()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Account not found.")
			);
		}
		
		@Test
		public void cannotUpdateAccountImproperPassword() throws Exception {
			Account accountA = DataUtil.createAccountA();
			accountService.addTouristAccount(accountA);
			
			accountA.setPassword("123"); // password too short.
		    String accountJson = objectMapper.writeValueAsString(accountA);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/account/update-account/" + accountA.getAccountId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(accountJson)
			).andExpect(
		    		MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Password must have at least 8 characters.")
			);
		}
		
		@Test
		public void cannotUpdateAccountImproperEmail() throws Exception {
			Account accountA = DataUtil.createAccountA();
			AccountResponse savedAccount = accountService.addTouristAccount(accountA);
			
			DetailRequest newRequest = new DetailRequest();
		    newRequest.setAccountId(savedAccount.getAccountId());
		    newRequest.setEmail("gmail.com"); // improper email.
		    
		    String accountJson = objectMapper.writeValueAsString(newRequest);
			
			mockMvc.perform(
					MockMvcRequestBuilders.patch("/account/update-details/" + accountA.getAccountId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(accountJson)
			).andExpect(
		    		MockMvcResultMatchers.status().isBadRequest()
			).andExpect(
					MockMvcResultMatchers.jsonPath("$.error").value("Please include a proper email address.")
			);
		}
	}
}
