package com.example.geco.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.geco.AbstractControllerTest;
import com.example.geco.DataUtil;
import com.example.geco.domains.Account;
import com.example.geco.domains.Attraction;
import com.example.geco.domains.Booking;
import com.example.geco.domains.Feedback;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class MainControllerTests extends AbstractControllerTest{
	
	@Test
	public void canGetAttractionsNumber() throws Exception {
		Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
	    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
	    
	    Attraction attractionA = DataUtil.createAttractionA();
	    Attraction attractionB = DataUtil.createAttractionB();
	    
	    attractionRepository.save(attractionA);
	    attractionRepository.save(attractionB);
	    
	    mockMvc.perform(
				MockMvcRequestBuilders.get("/home")
					.contentType(MediaType.APPLICATION_JSON)
		).andExpect(
				MockMvcResultMatchers.status().isOk()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.attractionNumber").value(2)
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.averageRating").value(0)
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.averageVisitor").value(0)
		);
	}
	
	@Test
	public void canGetAverageMonthlyVisitors() throws Exception {
		Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
	    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
	    
	    Booking bookingA = DataUtil.createBookingA(
	    		savedAccount.getAccountId(), 
	    		accountRepository, 
	    		packageInclusionRepository, 
	    		tourPackageRepository);
	    
	    Booking bookingB = DataUtil.createBookingB(
	    		savedAccount.getAccountId(), 
	    		accountRepository, 
	    		packageInclusionRepository, 
	    		tourPackageRepository);
	    
	    LocalDate bookingADate = LocalDate.now(); 
	    bookingA.setVisitDate(bookingADate);
	    
	    LocalDate bookingBDate = LocalDate.now().minusMonths(1);
	    bookingB.setVisitDate(bookingBDate);
	    
	    bookingRepository.save(bookingA);
	    bookingRepository.save(bookingB);
	    
	    mockMvc.perform(
				MockMvcRequestBuilders.get("/home")
					.contentType(MediaType.APPLICATION_JSON)
		).andExpect(
				MockMvcResultMatchers.status().isOk()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.attractionNumber").value(0)
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.averageRating").value(0)
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.averageVisitor").value(1)
		);
	}
	
	@Test
	public void canGetAverageYearlyVisitors() throws Exception {
		Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
	    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
		
	    
	    Booking bookingA = DataUtil.createBookingA(
	    		savedAccount.getAccountId(), 
	    		accountRepository, 
	    		packageInclusionRepository, 
	    		tourPackageRepository);
	    
	    Booking bookingB = DataUtil.createBookingB(
	    		savedAccount.getAccountId(), 
	    		accountRepository, 
	    		packageInclusionRepository, 
	    		tourPackageRepository);
	    
	    LocalDate bookingADate = LocalDate.now(); 
	    bookingA.setVisitDate(bookingADate);
	    
	    LocalDate bookingBDate = LocalDate.now().minusMonths(1);
	    bookingB.setVisitDate(bookingBDate);
	    
	    bookingRepository.save(bookingA);
	    bookingRepository.save(bookingB);
	    
	    double yearlyAverageVisitor = bookingService.getAverageVisitor("year");
	    assertEquals(yearlyAverageVisitor, 2.0,  0.0001);
	}
	
	@Test
	public void canGetAverageRating() throws Exception {
		Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
	    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
	    
	    Feedback feedback = DataUtil.createFeedbackA(savedAccount.getAccountId(), 
	    		accountRepository, 
	    		packageInclusionRepository, 
	    		tourPackageRepository, bookingRepository, 
	    		feedbackCategoryRepository);
	    
	    feedbackRepository.save(feedback);
	    
	    mockMvc.perform(
				MockMvcRequestBuilders.get("/home")
					.contentType(MediaType.APPLICATION_JSON)
		).andExpect(
				MockMvcResultMatchers.status().isOk()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.attractionNumber").value(0)
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.averageRating").value(feedback.getStars())
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.averageVisitor").value(1)
		);
	}
	
	@Test
	public void canDisplayHomeData() throws Exception {
		Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
	    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
	    
	    Attraction attractionA = DataUtil.createAttractionA();
	    Attraction attractionB = DataUtil.createAttractionB();
	    
	    attractionRepository.save(attractionA);
	    attractionRepository.save(attractionB);
	    
	    Booking bookingA = DataUtil.createBookingA(
	    		savedAccount.getAccountId(), 
	    		accountRepository, 
	    		packageInclusionRepository, 
	    		tourPackageRepository);
	    
	    Booking bookingB = DataUtil.createBookingB(
	    		savedAccount.getAccountId(), 
	    		accountRepository, 
	    		packageInclusionRepository, 
	    		tourPackageRepository);
	    
	    LocalDate bookingADate = LocalDate.now(); 
	    bookingA.setVisitDate(bookingADate);
	    
	    LocalDate bookingBDate = LocalDate.now().minusMonths(1);
	    bookingB.setVisitDate(bookingBDate);
	    
	    bookingRepository.save(bookingA);
	    bookingRepository.save(bookingB);
	    
	    Feedback feedback = Feedback.builder()
	    		.account(savedAccount)
	    		.booking(bookingA)
	    		.stars(4.3)
	    		.build();
	    
	    feedbackRepository.save(feedback);
	    
	    mockMvc.perform(
				MockMvcRequestBuilders.get("/home")
					.contentType(MediaType.APPLICATION_JSON)
		).andExpect(
				MockMvcResultMatchers.status().isOk()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.attractionNumber").value(2)
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.averageRating").value(feedback.getStars())
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.averageVisitor").value(1)
		);
	    
	}
}
