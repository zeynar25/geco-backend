package com.example.geco.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.geco.DataUtil;
import com.example.geco.domains.Account;
import com.example.geco.domains.UserDetail;
import com.example.geco.dto.SignupRequest;
import com.example.geco.repositories.AccountRepository;
import com.example.geco.repositories.UserDetailRepository;
import com.example.geco.services.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class MainControllerTests {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	UserDetailRepository userDetailRepository;
	
	@BeforeEach
	void setup() {
	    accountRepository.deleteAll();
	    userDetailRepository.deleteAll();
	}
	
	@Test
	public void canAddAccount() throws Exception {
		UserDetail detailA = DataUtil.createUserDetailA();
		Account accountA = DataUtil.createAccountA(detailA);

		SignupRequest request = new SignupRequest(accountA, detailA);
		String requestJson = objectMapper.writeValueAsString(request);
		
		mockMvc.perform(
				MockMvcRequestBuilders.post("/signup")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestJson)
		).andExpect(
				MockMvcResultMatchers.status().isCreated()
		);
	}
	
	@Test
	public void cannotAddAccount() throws Exception {
		UserDetail detailA = DataUtil.createUserDetailA();
		
		Account accountA = DataUtil.createAccountA(detailA);
		accountA.setPassword("123"); // too short for a password.
		

	    SignupRequest request = new SignupRequest(accountA, detailA);
	    String json = objectMapper.writeValueAsString(request);

	    mockMvc.perform(
	            MockMvcRequestBuilders.post("/signup")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(json)
	    ).andExpect(
	    		MockMvcResultMatchers.status().isBadRequest()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.error").value("Password must have at least 8 characters.")
		);
	}
	
	// to implement
	@Test
	public void canGetAverageMonthlyVisitors() throws Exception {
		
	}
	
	// to implement
	@Test
	public void canGetAverageYearlyVisitors() throws Exception {
		
	}
	
	// to implement
	@Test
	public void canGetAverageRating() throws Exception {
		
	}
	
	// to implement
	@Test
	public void canDisplayHomeData() throws Exception {
		
	}
}
