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
import com.example.geco.domains.Booking.BookingStatus;
import com.example.geco.domains.Feedback;
import com.example.geco.domains.TourPackage;
import com.example.geco.dto.AdminBookingRequest;

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
	
	@Test
	public void canGetCalendar() throws Exception {
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
	    

	    LocalDate bookingADate = LocalDate.of(2025, 11, 1); 
	    bookingA.setVisitDate(bookingADate);
	    
	    LocalDate bookingBDate = LocalDate.of(2025, 11, 10); 
	    bookingB.setVisitDate(bookingBDate);
	    
	    TourPackage tourPackage = TourPackage.builder()
	    		.build();
	    
	    TourPackage savedTourPackage = tourPackageRepository.save(tourPackage);
	    
	    Booking bookingC = Booking.builder()
	    		.account(savedAccount)
	    		.tourPackage(savedTourPackage)
	    		.visitDate(bookingBDate)
	    		.groupSize(10)
	    		.build();
	    
	   	bookingRepository.save(bookingA);
	   	bookingRepository.save(bookingB);
	   	bookingRepository.save(bookingC);
	    
	    mockMvc.perform(
				MockMvcRequestBuilders.get("/calendar/2025/11")
					.contentType(MediaType.APPLICATION_JSON)
		).andExpect(
				MockMvcResultMatchers.status().isOk()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.1.bookings").value(1)
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.1.visitors").value(bookingA.getGroupSize())
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.10.bookings").value(2)
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.10.visitors").value(bookingB.getGroupSize() + bookingC.getGroupSize())
		);
	}
	
	@Test
	public void canGetDashboardStatsMonthlyBooking() throws Exception {
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
	    
	    bookingA.setVisitDate(LocalDate.now());
	    bookingB.setVisitDate(LocalDate.now());
	    
	   	bookingRepository.save(bookingA);
	   	bookingRepository.save(bookingB);
	    
	    mockMvc.perform(
				MockMvcRequestBuilders.get("/dashboard")
					.contentType(MediaType.APPLICATION_JSON)
		).andExpect(
				MockMvcResultMatchers.status().isOk()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.monthlyBooking").value(2)
		);
	}
	
	@Test
	public void canGetDashboardStatsPreviousMonthlyBooking() throws Exception {
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
	    
	    bookingA.setVisitDate(LocalDate.now());
	    bookingB.setVisitDate(LocalDate.now().minusMonths(1));
	    
	   	bookingRepository.save(bookingA);
	   	bookingRepository.save(bookingB);
	    
	    mockMvc.perform(
				MockMvcRequestBuilders.get("/dashboard")
					.contentType(MediaType.APPLICATION_JSON)
		).andExpect(
				MockMvcResultMatchers.status().isOk()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.monthlyBooking").value(1)
		);
	}
	

	@Test
	public void canGetDashboardStatsMonthlyRevenue() throws Exception {
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
	    
	    bookingA.setVisitDate(LocalDate.now());
	    bookingB.setVisitDate(LocalDate.now());
	    
	    bookingA.setBookingStatus(BookingStatus.COMPLETED);
	    bookingB.setBookingStatus(BookingStatus.COMPLETED);
	    
	   	bookingRepository.save(bookingA);
	   	bookingRepository.save(bookingB);
	    
	    mockMvc.perform(
				MockMvcRequestBuilders.get("/dashboard")
					.contentType(MediaType.APPLICATION_JSON)
		).andExpect(
				MockMvcResultMatchers.status().isOk()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.monthlyRevenue")
				.value(bookingA.getTotalPrice() + bookingB.getTotalPrice())
		);
	}
	
	@Test
	public void canGetDashboardStatsPendingBookings() throws Exception {
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
	    
	    bookingA.setVisitDate(LocalDate.now());
	    bookingB.setVisitDate(LocalDate.now());
	    
	   	bookingRepository.save(bookingA);
	   	bookingRepository.save(bookingB);
	    
	    mockMvc.perform(
				MockMvcRequestBuilders.get("/dashboard")
					.contentType(MediaType.APPLICATION_JSON)
		).andExpect(
				MockMvcResultMatchers.status().isOk()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.pendingBookings").value(2)
		);
	}
	
	@Test
	public void canGetDashboardStatsNewFeedbacks() throws Exception {
		Account savedAccount = DataUtil.createAdminAccountA(accountRepository);
	    mockAdminAuthentication(savedAccount.getAccountId(), savedAccount.getDetail().getEmail());
	    
	    Feedback feedbackA = DataUtil.createFeedbackA(
	    		savedAccount.getAccountId(), 
	    		accountRepository, 
	    		packageInclusionRepository, 
	    		tourPackageRepository, 
	    		bookingRepository, 
	    		feedbackCategoryRepository);
	    
	    Feedback feedbackB = DataUtil.createFeedbackB(
	    		savedAccount.getAccountId(), 
	    		accountRepository, 
	    		packageInclusionRepository, 
	    		tourPackageRepository, 
	    		bookingRepository, 
	    		feedbackCategoryRepository);
	    
	   	feedbackRepository.save(feedbackA);
	   	feedbackRepository.save(feedbackB);
	    
	    mockMvc.perform(
				MockMvcRequestBuilders.get("/dashboard")
					.contentType(MediaType.APPLICATION_JSON)
		).andExpect(
				MockMvcResultMatchers.status().isOk()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$.unreadFeedback").value(2)
		);
	}
	
	@Test
	public void canGetDashboardStatsDashboardBookings() throws Exception {
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
	    
	    bookingA.setVisitDate(LocalDate.now());
	    bookingB.setVisitDate(LocalDate.now());
	    bookingB.setVisitTime(bookingA.getVisitTime().plusMinutes(1));
	    
	   	Booking savedBookingA = bookingRepository.save(bookingA);
	   	Booking savedBookingB = bookingRepository.save(bookingB);
	   	
	   	AdminBookingRequest request = AdminBookingRequest.builder()
	   			.email(savedAccount.getDetail().getEmail())
	   			.status(BookingStatus.PENDING)
	   			.build();

		String requestJson = objectMapper.writeValueAsString(request);
	    
	    mockMvc.perform(
				MockMvcRequestBuilders.post("/dashboard/bookings")
					.contentType(MediaType.APPLICATION_JSON)
					.content(requestJson)
		).andExpect(
				MockMvcResultMatchers.status().isOk()
		).andExpect(
				MockMvcResultMatchers.jsonPath("$[0].bookingId").value(savedBookingA.getBookingId())
		).andExpect(
				MockMvcResultMatchers.jsonPath("$[1].bookingId").value(savedBookingB.getBookingId())
		);
	}
}
