package com.example.geco.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.geco.domains.Account;
import com.example.geco.domains.Booking;
import com.example.geco.domains.Booking.BookingStatus;
import com.example.geco.domains.Feedback.FeedbackStatus;
import com.example.geco.domains.TourPackage;
import com.example.geco.dto.AdminBookingRequest;
import com.example.geco.dto.AdminDashboardFinances;
import com.example.geco.dto.AdminDashboardStats;
import com.example.geco.dto.ChartData;
import com.example.geco.repositories.AccountRepository;
import com.example.geco.repositories.BookingRepository;
import com.example.geco.repositories.TourPackageRepository;

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
	
	@Autowired
	private TourPackageRepository packageRepository;

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
			return bookingRepository.findByBookingStatusOrderByVisitDateAscVisitTimeAsc(status);
		}
		
		Account account = accountRepository.findByDetailEmail(email.strip())
			.orElseThrow(() -> new UsernameNotFoundException("Account with Email \"" + email + "\" not found."));
		
		if (status == null) {
			return bookingRepository.findByAccount_AccountIdOrderByVisitDateAscVisitTimeAsc(account.getAccountId());
		}
		
		return bookingRepository.findByAccount_AccountIdAndBookingStatusOrderByVisitDateAscVisitTimeAsc(account.getAccountId(), status);
	}

	public AdminDashboardFinances getDashboardFinance(Integer year, Integer month) {
		long totalBookings = 0;
		long completedBookings = 0;

		long totalRevenue = 0;
		long averageRevenuePerBooking = 0;
		
		// When there's no provided year and month, it means all.
		if (year == null && month == null) {
			totalBookings = (int) bookingRepository.count();
			totalRevenue = bookingRepository.findTotalRevenueByStatus(BookingStatus.COMPLETED);
			completedBookings = bookingRepository.countByBookingStatus(BookingStatus.COMPLETED).intValue();
			
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
	        completedBookings = bookingRepository.countByBookingStatusAndVisitDateBetween(BookingStatus.COMPLETED, startDate, endDate);
	        totalRevenue = bookingRepository.getTotalRevenueByBookingStatusAndVisitDateBetween(startDate, endDate, BookingStatus.COMPLETED);
			
		} else {
			LocalDate startDate = LocalDate.of(year, month, 1);
			LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
			
			totalBookings = bookingRepository.countByVisitDateBetween(startDate, endDate);
			totalRevenue = bookingRepository.getTotalRevenueByBookingStatusAndVisitDateBetween(startDate, endDate, BookingStatus.COMPLETED);
			completedBookings = bookingRepository.countByBookingStatusAndVisitDateBetween(BookingStatus.COMPLETED, startDate, endDate);		
		}

		averageRevenuePerBooking = (completedBookings == 0) ? 0 : totalRevenue / completedBookings;
		
		return AdminDashboardFinances.builder()
			    .totalRevenue(totalRevenue)
			    .averageRevenuePerBooking(averageRevenuePerBooking)
			    .totalBookings(totalBookings)
			    .completedBookings(completedBookings)
			    .build();
	}

	public List<ChartData> getYearlyBookings(Integer startYear, Integer endYear) {
		if (startYear == null) {
			startYear = bookingRepository.getEarliestYear();
		}
		
		if (endYear == null) {
			endYear = bookingRepository.getLatestYear();
		}
		
		List<ChartData> yearlyBookings = new ArrayList<>();
		for (int year = startYear; year <= endYear; year++) {
			LocalDate startDate = LocalDate.of(year, 1, 1);
	        LocalDate endDate = LocalDate.of(year, 12, 31);
	        
	        yearlyBookings.add(
					ChartData.builder()
					.period(String.valueOf(year))
					.value(bookingRepository.countByBookingStatusAndVisitDateBetween(
							BookingStatus.COMPLETED,
							startDate,
							endDate))
					.build()
					);
		}
		
		return yearlyBookings;
	}

	public List<ChartData> getMonthlyBookings(Integer year) {
		if (year == null) {
			throw new IllegalArgumentException("Please provide a year.");
		}
		
		List<ChartData> getMonthlyBookings = new ArrayList<>();
		for (int month = 1; month <= 12; month++) {
			YearMonth yearMonth = YearMonth.of(year, month);
		    LocalDate startDate = yearMonth.atDay(1);
		    LocalDate endDate = yearMonth.atEndOfMonth();
	        
		    getMonthlyBookings.add(
					ChartData.builder()
					.period(yearMonth.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
					.value(bookingRepository.countByBookingStatusAndVisitDateBetween(
							BookingStatus.COMPLETED,
							startDate,
							endDate))
					.build()
					);
		}
		
		return getMonthlyBookings;
	}

	public List<ChartData> getYearlyVisitors(Integer startYear, Integer endYear) {
		if (startYear == null) {
			startYear = bookingRepository.getEarliestYear();
		}
		
		if (endYear == null) {
			endYear = bookingRepository.getLatestYear();
		}
		
		List<ChartData> yearlyVisitors = new ArrayList<>();
		
		for (int year = startYear; year <= endYear; year++) {
			LocalDate startDate = LocalDate.of(year, 1, 1);
	        LocalDate endDate = LocalDate.of(year, 12, 31);
	        
	        yearlyVisitors.add(
					ChartData.builder()
					.period(String.valueOf(year))
					.value(bookingRepository.totalGroupSizeByStatusAndVisitDateBetween(
							BookingStatus.COMPLETED,
							startDate,
							endDate))
					.build()
					);
		}
		return yearlyVisitors;
	}
	
	public List<ChartData> getMonthlyVisitors(Integer year) {
		if (year == null) {
			throw new IllegalArgumentException("Please provide a year.");
		}
		
		List<ChartData> monthlyVisitors = new ArrayList<>();
		for (int month = 1; month <= 12; month++) {
			YearMonth yearMonth = YearMonth.of(year, month);
		    LocalDate startDate = yearMonth.atDay(1);
		    LocalDate endDate = yearMonth.atEndOfMonth();
	        
		    monthlyVisitors.add(
					ChartData.builder()
					.period(yearMonth.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
					.value(bookingRepository.totalGroupSizeByStatusAndVisitDateBetween(
							BookingStatus.COMPLETED,
							startDate,
							endDate))
					.build()
					);
		}
		
		return monthlyVisitors;
	}

	public List<ChartData> getAvailedPackages(Integer startYear, Integer endYear) {
		if (startYear == null) {
			startYear = bookingRepository.getEarliestYear();
		}
		
		if (endYear == null) {
			endYear = bookingRepository.getLatestYear();
		}
		
		LocalDate startDate = LocalDate.of(startYear, 1, 1);
        LocalDate endDate = LocalDate.of(endYear, 12, 31);
		
		List<ChartData> availedPackages = new ArrayList<>();
		List<TourPackage> tourPackages = packageRepository.findAll();

		for (TourPackage tourPackage : tourPackages) {
			availedPackages.add(
					ChartData.builder()
					.period(tourPackage.getName())
					.value(bookingRepository.countByTourPackageAndVisitDateBetween(
							tourPackage,
							startDate,
							endDate))
					.build()
					);
		}
		
		return availedPackages;
	}
}
