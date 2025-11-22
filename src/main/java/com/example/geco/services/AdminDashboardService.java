package com.example.geco.services;

import java.time.LocalDate;
import java.time.YearMonth;
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
		int totalBookings = 0;
		int completedBookings = 0;

		long totalRevenue = 0;
		long averageRevenuePerBooking = 0;
		
		// When there's no provided year and month, it means all.
		if (year == null && month == null) {
			totalBookings = (int) bookingRepository.count();
			totalRevenue = bookingRepository.findTotalRevenueByStatus(BookingStatus.COMPLETED);
			completedBookings = bookingRepository.countByStatus(BookingStatus.COMPLETED).intValue();
			
		// When there's no provided year but there's a month, it means data on that month across years.
		} else if (year == null) {
			int startYear = bookingRepository.getEarliestYear();
			int endYear = bookingRepository.getLatestYear();

			Object[] stats = bookingRepository.getMonthAcrossYearsStats(month, startYear, endYear);

		    totalBookings = ((Number) stats[0]).intValue();
		    completedBookings = ((Number) stats[1]).intValue();
		    totalRevenue = ((Number) stats[2]).longValue();

		// When there's a provided year but there no month, it means data of months on that year.
		} else if (month == null) {
			LocalDate startDate = LocalDate.of(year, 1, 1);
	        LocalDate endDate = LocalDate.of(year, 12, 31);

	        totalBookings = bookingRepository.countByVisitDateBetween(startDate, endDate);
	        completedBookings = bookingRepository.countByStatusAndVisitDateBetween(BookingStatus.COMPLETED, startDate, endDate);
	        totalRevenue = bookingRepository.getRevenue(startDate, endDate, BookingStatus.COMPLETED);
			
		} else {
			LocalDate startDate = LocalDate.of(year, month, 1);
			LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
			
			totalBookings = bookingRepository.countByVisitDateBetween(startDate, endDate);
			totalRevenue = bookingRepository.getRevenue(startDate, endDate, BookingStatus.COMPLETED);
			completedBookings = bookingRepository.countByStatusAndVisitDateBetween(BookingStatus.COMPLETED, startDate, endDate);		
		}

		averageRevenuePerBooking = (completedBookings == 0) ? 0 : totalRevenue / completedBookings;
		
		return AdminDashboardFinances.builder()
			    .totalRevenue(totalRevenue)
			    .averageRevenuePerBooking(averageRevenuePerBooking)
			    .totalBookings(totalBookings)
			    .completedBookings(completedBookings)
			    .build();
	}
}
