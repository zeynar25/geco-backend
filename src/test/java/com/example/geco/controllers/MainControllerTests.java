package com.example.geco.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import com.example.geco.domains.Attraction;
import com.example.geco.domains.UserDetail;
import com.example.geco.dto.SignupRequest;
import com.example.geco.repositories.AccountRepository;
import com.example.geco.repositories.AttractionRepository;
import com.example.geco.repositories.UserDetailRepository;
import com.example.geco.services.AccountService;
import com.example.geco.services.AttractionService;
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
	AttractionService attractionService;
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	UserDetailRepository userDetailRepository;
	
	@Autowired
	AttractionRepository attractionRepository;
	
	@BeforeEach
	void setup() {
	    accountRepository.deleteAll();
	    userDetailRepository.deleteAll();
	    attractionRepository.deleteAll();
	}
	
	@Test
	public void canAddAccount() throws Exception {
		UserDetail detailA = DataUtil.createUserDetailA();
		Account accountA = DataUtil.createAccountA(detailA);

		SignupRequest request = new SignupRequest(accountA, detailA);
		String requestJson = objectMapper.writeValueAsString(request);
		
		mockMvc.perform(
				MockMvcRequestBuilders.post("/account")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestJson)
		).andExpect(
				MockMvcResultMatchers.status().isCreated()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.passwordNotice").exists()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.surname").value(detailA.getSurname())
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.firstName").value(detailA.getFirstName())
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.email").value(detailA.getEmail())
		);
	}
	
	@Test
	public void cannotAddAccount() throws Exception {
		UserDetail detailA = DataUtil.createUserDetailA();
		
		Account accountA = DataUtil.createAccountA(detailA);
		accountA.setPassword("123"); // too short for a password.
		

	    SignupRequest request = new SignupRequest(accountA, detailA);
	    String requestJson = objectMapper.writeValueAsString(request);

	    mockMvc.perform(
	            MockMvcRequestBuilders.post("/account")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(requestJson)
	    ).andExpect(
	    		MockMvcResultMatchers.status().isBadRequest()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.error").value("Password must have at least 8 characters.")
		);
	}
	
	@Test
	public void canUpdateAccount() throws Exception {
		UserDetail detailA = DataUtil.createUserDetailA();
		Account accountA = DataUtil.createAccountA(detailA);
		
		SignupRequest request = new SignupRequest(accountA, detailA);
		accountService.addAccount(request);
		
		detailA.setEmail("new@gmail.com");
		SignupRequest newRequest = new SignupRequest(accountA, detailA);
	    String requestJson = objectMapper.writeValueAsString(newRequest);
		
		mockMvc.perform(
				MockMvcRequestBuilders.patch("/account")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestJson)
		).andExpect(
				MockMvcResultMatchers.status().isOk()
		).andExpect(
			MockMvcResultMatchers.jsonPath("$.passwordNotice").exists()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.surname").value(detailA.getSurname())
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.firstName").value(detailA.getFirstName())
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.email").value(detailA.getEmail())
		);
	}
	
	@Test
	public void cannotUpdateAccountWithNotAccountNotFound() throws Exception {
		UserDetail detailA = DataUtil.createUserDetailA();
		Account accountA = DataUtil.createAccountA(detailA);
		
		SignupRequest request = new SignupRequest(accountA, detailA);
		// Did not save request through accountService.
		
		accountA.setPassword("123321123321");
		SignupRequest newRequest = new SignupRequest(accountA, detailA);
	    String requestJson = objectMapper.writeValueAsString(newRequest);
		
		mockMvc.perform(
				MockMvcRequestBuilders.patch("/account")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestJson)
		).andExpect(
	    		MockMvcResultMatchers.status().isBadRequest()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.error").value("Account not found.")
		);
	}
	
	@Test
	public void cannotUpdateAccountWithImproperPassword() throws Exception {
		UserDetail detailA = DataUtil.createUserDetailA();
		Account accountA = DataUtil.createAccountA(detailA);
		
		SignupRequest request = new SignupRequest(accountA, detailA);
		accountService.addAccount(request);
		
		accountA.setPassword("123"); // password too short.
		SignupRequest newRequest = new SignupRequest(accountA, detailA);
	    String requestJson = objectMapper.writeValueAsString(newRequest);
		
		mockMvc.perform(
				MockMvcRequestBuilders.patch("/account")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestJson)
		).andExpect(
	    		MockMvcResultMatchers.status().isBadRequest()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.error").value("Password must have at least 8 characters.")
		);
	}
	
	@Test
	public void cannotUpdateAccountWithImproperEmail() throws Exception {
		UserDetail detailA = DataUtil.createUserDetailA();
		Account accountA = DataUtil.createAccountA(detailA);
		
		SignupRequest request = new SignupRequest(accountA, detailA);
		accountService.addAccount(request);
		
		detailA.setEmail("gmail.com"); // improper email.
		SignupRequest newRequest = new SignupRequest(accountA, detailA);
	    String requestJson = objectMapper.writeValueAsString(newRequest);
		
		mockMvc.perform(
				MockMvcRequestBuilders.patch("/account")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestJson)
		).andExpect(
	    		MockMvcResultMatchers.status().isBadRequest()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.error").value("Please include a proper email address.")
		);
	}
	
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
	public void canNotAddAttractionWithoutTitle() throws Exception {
		Attraction attraction = new Attraction();
	    attraction.setName(""); // invalid title
	    attraction.setDescription("This is a valid description with at least 10 characters.");
	    String attractionJson = objectMapper.writeValueAsString(attraction);
	    
	    
	    mockMvc.perform(
				MockMvcRequestBuilders.post("/attraction")
					.contentType(MediaType.APPLICATION_JSON)
					.content(attractionJson)
		).andExpect(
				MockMvcResultMatchers.status().isBadRequest()
		).andExpect(
				MockMvcResultMatchers.content().string("Attraction name must have at least 1 character.")
		);
	}
	
	@Test
	public void canNotAddAttractionWithoutDescription() throws Exception {
		Attraction attraction = new Attraction();
	    attraction.setName("valid title"); // invalid title
	    attraction.setDescription("too short");
	    String attractionJson = objectMapper.writeValueAsString(attraction);
	    
	    mockMvc.perform(
				MockMvcRequestBuilders.post("/attraction")
					.contentType(MediaType.APPLICATION_JSON)
					.content(attractionJson)
		).andExpect(
				MockMvcResultMatchers.status().isBadRequest()
		).andExpect(
				MockMvcResultMatchers.content().string("Attraction description must be at least 10 characters long.")
		);
	}
	
	@Test
	public void canGetAttraction() throws Exception {
		Attraction attractionA = DataUtil.createAttractionA();
		Attraction savedAttractionA = attractionService.addAttraction(attractionA);

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
		Attraction savedAttractionA = attractionService.addAttraction(attractionA);

		Attraction attractionB = DataUtil.createAttractionB();
		Attraction savedAttractionB = attractionService.addAttraction(attractionB);
		
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
	
	// to implement
	@Test
	public void canUpdateAttraction() throws Exception {
		
	}
	
	@Test
	public void canDeleteAttraction() throws Exception {
		
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
