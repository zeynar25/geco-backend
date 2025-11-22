package com.example.geco.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.geco.domains.Account;
import com.example.geco.domains.Booking;
import com.example.geco.domains.Booking.BookingStatus;
import com.example.geco.domains.Feedback.FeedbackStatus;
import com.example.geco.dto.AdminBookingRequest;
import com.example.geco.dto.AdminDashboardFinances;
import com.example.geco.dto.AdminDashboardStats;
import com.example.geco.repositories.AccountRepository;
import com.example.geco.repositories.BookingRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AdminDashboardService {
	@Autowired
	private BookingService bookingService;
	
	@Autowired
	private FeedbackService feedbackService;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private AccountRepository accountRepository;

	public AdminDashboardStats getDashboardStats(LocalDate date) {
		return new AdminDashboardStats(
				bookingService.getNumberOfBookingByMonth(LocalDate.now()),
				bookingService.getMonthRevenue(LocalDate.now()),
				bookingService.getNumberOfPendingBookings(),
				feedbackService.getNumberOfNewFeedbacks(FeedbackStatus.NEW)
		);
	}

	public List<Booking> getBookingByAdmin(AdminBookingRequest request) {
		String email = request.getName() != null ? request.getName().strip() : null;
		BookingStatus status = request.getStatus();
		
		if ((email == null || email.isBlank()) && status == null) {
			return bookingRepository.findAllByOrderByVisitDateAscVisitTimeAsc();
		}
		
		if (email == null || email.isBlank()) {
			return bookingRepository.findByStatusOrderByVisitDateAscVisitTimeAsc(status);
		}
		
		Account account = accountRepository.findByDetailEmail(email.strip()); 
		if (account == null) {
		    throw new EntityNotFoundException("Account with Email \"" + email + "\" not found.");
		}
		
		if (status == null) {
			return bookingRepository.findByAccount_AccountIdOrderByVisitDateAscVisitTimeAsc(account.getAccountId());
		}
		
		return bookingRepository.findByAccount_AccountIdAndStatusOrderByVisitDateAscVisitTimeAsc(account.getAccountId(), status);
	}


	public AdminDashboardFinances getDashboardFinance(Integer year, Integer month) {
		return new AdminDashboardFinances();
	}
}
