package com.example.geco.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.geco.domains.Account;
import com.example.geco.domains.AuditLog.LogAction;
import com.example.geco.domains.Booking;
import com.example.geco.domains.Booking.BookingStatus;
import com.example.geco.domains.Booking.PaymentStatus;
import com.example.geco.domains.BookingInclusion;
import com.example.geco.domains.PackageInclusion;
import com.example.geco.domains.TourPackage;
import com.example.geco.dto.BookingInclusionRequest;
import com.example.geco.dto.BookingRequest;
import com.example.geco.dto.BookingUpdateRequest;
import com.example.geco.dto.CalendarDay;
import com.example.geco.dto.ChartData;
import com.example.geco.dto.UserBookingUpdateRequest;
import com.example.geco.repositories.AccountRepository;
import com.example.geco.repositories.BookingRepository;
import com.example.geco.repositories.PackageInclusionRepository;
import com.example.geco.repositories.TourPackageRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class BookingService extends BaseService{
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private TourPackageRepository tourPackageRepository;
	
	@Autowired
	private PackageInclusionRepository inclusionRepository;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	private Booking createBookingCopy(Booking booking) {
		return Booking.builder()
				.bookingId(booking.getBookingId())
				.account(booking.getAccount())
				.tourPackage(booking.getTourPackage())
				.bookingInclusions(booking.getBookingInclusions())
				.visitDate(booking.getVisitDate())
				.visitTime(booking.getVisitTime())
				.groupSize(booking.getGroupSize())
				.bookingStatus(booking.getBookingStatus())
				.paymentStatus(booking.getPaymentStatus())
				.totalPrice(booking.getTotalPrice())
				.isActive(booking.isActive())
				.build();
	}
	
	private void validateVisitDate(LocalDate visitDate) {
		LocalDate today = LocalDate.now();
		
		if (!visitDate.isAfter(today.plusDays(1))) {
		    throw new IllegalArgumentException("Booking visit date must be at least 2 days from today.");
		}
	}
	
	private void validateVisitTime(Integer id, Integer tourDurationMinutes, LocalDate visitDate, LocalTime visitTime) {
		// Park opens by 8:00, giving time to prepare for the tour by 9:00.
		LocalTime startTime = LocalTime.of(9, 0);
		
		// Ensure tour finishes by 16:00, giving time to close by 17:00
		LocalTime endTime = LocalTime.of(16, 0).minusMinutes(tourDurationMinutes); 
		
		if (visitTime.isBefore(startTime) || visitTime.isAfter(endTime)) {
			throw new IllegalArgumentException("Booking visit time must be between 9:00 and end at by 16:00.");
		}
		
	    // Check if there's an overlap in schedule and selected schedule.
		checkScheduleOverlap(
				id,
				visitDate, 
				visitTime,
				visitTime.plusMinutes(tourDurationMinutes)
		);
	}
	
	private void checkScheduleOverlap(Integer id, LocalDate visitDate, LocalTime requestedStart, LocalTime requestedEnd) {
		List<Booking> bookingsOnDate = new ArrayList<>();
		if (id != null ) {
			 bookingsOnDate = bookingRepository.findByVisitDateAndBookingIdNotOrderByVisitTimeAsc(visitDate, id);
			 
		} else {
			 bookingsOnDate = bookingRepository.findByVisitDateOrderByVisitTimeAsc(visitDate);
		}
				
		for (Booking existing : bookingsOnDate) {
		    int existingDuration = existing.getTourPackage().getDuration() != null
		            ? existing.getTourPackage().getDuration()
		            : 0;
		    LocalTime existingStart = existing.getVisitTime();
		    LocalTime existingEnd = existingStart.plusMinutes(existingDuration);

		    // Check if times overlap
		    boolean overlap = !requestedEnd.isBefore(existingStart) && !requestedStart.isAfter(existingEnd);
		    if (overlap) {
		        throw new IllegalArgumentException(
		            "Selected time overlaps with an existing booking from " + existingStart + " to " + existingEnd + "."
		        );
		    }
	    }
	}
	
	private int getTotalInclusionPrice(List<BookingInclusionRequest> requests, int groupSize) {
	    if (requests == null || requests.isEmpty()) {
	        return 0;
	    }

	    // Fetch all PackageInclusions for the given inclusion IDs.
	    List<Integer> inclusionIds = requests
	            .stream()
	            .map(BookingInclusionRequest::getInclusionId)
	            .toList();

	    List<PackageInclusion> inclusions = inclusionRepository.findAllByInclusionIdIn(inclusionIds);

	    if (inclusions.size() != inclusionIds.size()) {
	        throw new IllegalArgumentException("Some inclusion IDs do not exist.");
	    }
	    
	    // Map inclusions by ID for easy lookup.
	    Map<Integer, PackageInclusion> inclusionMap = inclusions.stream()
	            .collect(Collectors.toMap(PackageInclusion::getInclusionId, pi -> pi));


	    int totalPrice = 0;
	    
	    for (BookingInclusionRequest reqInclusion : requests) {
	        PackageInclusion inclusion = inclusionMap.get(reqInclusion.getInclusionId());
	        if (inclusion == null) {
	            throw new IllegalArgumentException("Inclusion ID " + reqInclusion.getInclusionId() + " not found.");
	        }

	        if (reqInclusion.getQuantity() > groupSize) {
	            throw new IllegalArgumentException("Inclusion quantity cannot exceed booking group size.");
	        }

	        totalPrice += inclusion.getInclusionPricePerPerson() * reqInclusion.getQuantity();
	    }

	    return totalPrice;
	}
	
	private int getTotalInclusionPrice(Booking booking) {
	    if (booking.getBookingInclusions() == null || booking.getBookingInclusions().isEmpty()) {
	        return 0;
	    }

	    int totalPrice = 0;
	    for (BookingInclusion inclusion : booking.getBookingInclusions()) {
	        if (inclusion.getPriceAtBooking() == null || inclusion.getQuantity() == null) {
	            throw new IllegalArgumentException("Booking Inclusion must have a price and quantity.");
	        }
	        if (inclusion.getQuantity() > booking.getGroupSize()) {
	            throw new IllegalArgumentException("Inclusion quantity cannot exceed booking group size.");
	        }

	        totalPrice += inclusion.getPriceAtBooking() * inclusion.getQuantity();
	    }

	    return totalPrice;
	}

	
	public Booking addBooking(BookingRequest request) {
		int accountId = request.getAccountId();
		int tourPackageId = request.getTourPackageId();
		
		List<BookingInclusionRequest> bookingInclusionRequests = 
			    request.getBookingInclusionRequests() != null 
			    ? request.getBookingInclusionRequests() 
			    		: Collections.emptyList();
		
		LocalDate visitDate = request.getVisitDate();
		LocalTime visitTime = request.getVisitTime();
		int groupSize = request.getGroupSize();
		
		checkAuth(accountId);
  
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new EntityNotFoundException("Account with ID '" + accountId + "' not found."));
		
		TourPackage tourPackage = tourPackageRepository.findById(tourPackageId)
				.orElseThrow(() -> new EntityNotFoundException("Tour package with ID '" + accountId + "' not found."));
		
		List<Integer> bookingInclusionIds = new ArrayList<>();
		for (BookingInclusionRequest bookingInclusionRequest : bookingInclusionRequests) {
			bookingInclusionIds.add(bookingInclusionRequest.getInclusionId());
		}
		
		List<PackageInclusion> packageInclusions =
				inclusionRepository.findAllByInclusionIdIn(bookingInclusionIds);

	    if (packageInclusions.size() != bookingInclusionIds.size()) {
	        throw new IllegalArgumentException("Some inclusion IDs do not exist.");
	    }

		validateVisitDate(visitDate);
		validateVisitTime(null, tourPackage.getDuration(), visitDate, visitTime);
	    
		// Initial build of booking.
	    Booking booking = Booking.builder()
	            .account(account)
	            .tourPackage(tourPackage)
	            .visitDate(visitDate)
	            .visitTime(visitTime)
	            .groupSize(groupSize)
	            .bookingStatus(Booking.BookingStatus.PENDING)
	            .paymentStatus(Booking.PaymentStatus.UNPAID)
	            .totalPrice(
	            		(tourPackage.getBasePrice() * groupSize) 
	            		+ getTotalInclusionPrice(request.getBookingInclusionRequests(), groupSize)
	            )
	            .build();

	    // Set booking reference for inclusions
	    List<BookingInclusion> inclusions = new ArrayList<>();
	    for (BookingInclusionRequest reqInclusion : bookingInclusionRequests) {
	        PackageInclusion inclusion = packageInclusions.stream()
	                .filter(pi -> pi.getInclusionId().equals(reqInclusion.getInclusionId()))
	                .findFirst()
	                .orElseThrow();

	        inclusions.add(BookingInclusion.builder()
	                .booking(booking)
	                .inclusion(inclusion)
	                .quantity(reqInclusion.getQuantity())
	                .priceAtBooking(inclusion.getInclusionPricePerPerson())
	                .build());
	    }
	    
	    booking.setBookingInclusions(inclusions);
		Booking savedBooking = bookingRepository.save(booking);
		
		logIfStaffOrAdmin("Booking", (long) savedBooking.getBookingId(), LogAction.CREATE, null, savedBooking);
		
		return savedBooking;
	}

	@Transactional(readOnly = true)
	public Booking getBooking(int id) {
		return bookingRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Booking with ID '" + id + "' not found."));
	}

	@Transactional(readOnly = true)
	public List<Booking> getBookingByAccountAndDateRange(
			Integer accountId, 
	        LocalDate startDate, 
	        LocalDate endDate) {
	    if (accountId == null && startDate == null && endDate == null) {
	    	return bookingRepository.findAll();
	    } 

	    if (accountId == null) {
	    	return bookingRepository.findByVisitDateBetween(startDate, endDate);
	    }
	    
	    if (startDate == null && endDate == null) {
	    	return bookingRepository.findByAccount_AccountIdOrderByVisitDateAscVisitTimeAsc(accountId);
	    } 
	    
    	return bookingRepository.findByAccount_AccountIdAndVisitDateBetween(
    			accountId, startDate, endDate);
	}

	@Transactional(readOnly = true)
	public List<Booking> getActiveBookingByAccountAndDateRange(
			Integer accountId, 
	        LocalDate startDate, 
	        LocalDate endDate) {
	    if (accountId == null && startDate == null && endDate == null) {
	    	return bookingRepository.findAllByIsActive(true);
	    } 

	    if (accountId == null) {
	    	return bookingRepository.findByIsActiveAndVisitDateBetween(true, startDate, endDate);
	    }
	    
	    if (startDate == null && endDate == null) {
	    	return bookingRepository.findByAccount_AccountIdAndIsActiveOrderByVisitDateAscVisitTimeAsc(accountId, true);
	    } 
	    
    	return bookingRepository.findByAccount_AccountIdAndIsActiveAndVisitDateBetween(
    			accountId, true, startDate, endDate);
	}
	
	@Transactional(readOnly = true)
	public List<Booking> getMyBookingByDateRange(
			LocalDate startDate, 
	        LocalDate endDate) {
	    return getBookingByAccountAndDateRange(getLoggedAccountId(), startDate, endDate);
	}

	@Transactional(readOnly = true)
	public List<Booking> getInactiveBookingByAccountAndDateRange(
			Integer accountId, 
	        LocalDate startDate, 
	        LocalDate endDate) {
	    if (accountId == null && startDate == null && endDate == null) {
	    	return bookingRepository.findAllByIsActive(false);
	    } 

	    if (accountId == null) {
	    	return bookingRepository.findByIsActiveAndVisitDateBetween(false, startDate, endDate);
	    }
	    
	    if (startDate == null && endDate == null) {
	    	return bookingRepository.findByAccount_AccountIdAndIsActiveOrderByVisitDateAscVisitTimeAsc(accountId, false);
	    } 
	    
    	return bookingRepository.findByAccount_AccountIdAndIsActiveAndVisitDateBetween(
    			accountId, false, startDate, endDate);
	}

	@Transactional(readOnly = true)
	public double getAverageVisitor(String type) {
		Iterable<Booking> iterable = bookingRepository.findAllByOrderByVisitDateAscVisitTimeAsc();	
		List<Booking> bookings = StreamSupport
		        .stream(iterable.spliterator(), false)
		        .collect(Collectors.toList());
		
		if(bookings.isEmpty()) return 0;
		
		int minYear = bookings.get(0).getVisitDate().getYear();
		int maxYear = bookings.get(bookings.size() - 1).getVisitDate().getYear();;
		int minMonth = bookings.get(0).getVisitDate().getMonthValue();
		int maxMonth = bookings.get(bookings.size() - 1).getVisitDate().getMonthValue();

		double totalAvgVisitors = 0;
		int totalMonth = 0;
		int index = 0;
		
		for (int year = minYear; year <= maxYear; year++) {
			int startMonth = (year == minYear) ? minMonth : 1;
		    int endMonth = (year == maxYear) ? maxMonth : 12;
			
			for (int month = startMonth; month <= endMonth; month++) {
				int visitors = 0;
				
				while (index < bookings.size()) {
					// If we go beyond current year and month, break.
					LocalDate currDate = bookings.get(index).getVisitDate();
					
					if (currDate.getYear() > year ||
							(currDate.getYear() == year && currDate.getMonthValue() > month)) {
						break;
					}
					
					visitors++;
					index++;
				}
				totalMonth++;
				totalAvgVisitors += (double) visitors / 30.0;
			}
		}

		double n = 0;
		if (type.equals("year")) {
			n = totalAvgVisitors / (maxYear - minYear + 1);
			
		} else if (type.equals("month")) {
			n = totalAvgVisitors / totalMonth;
		}
			
		return n;
	}

	@Transactional(readOnly = true)
	public Map<Integer, CalendarDay> getCalendar(int year, int month) {
		if (year <= 0) {
	        throw new IllegalArgumentException("Invalid year.");
		}
		
		if (month <= 0 || month > 12) {
	        throw new IllegalArgumentException("Invalid month.");
		}
		
		Map<Integer, CalendarDay> calendar = new HashMap<>();
		
		YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        
    	List<Booking> allBookings = bookingRepository.findByVisitDateBetween(
    		    LocalDate.of(year, month, 1),
    		    LocalDate.of(year, month, daysInMonth)
    		);
    	
    	Map<LocalDate, List<Booking>> bookingsByDate = allBookings.stream()
    		    .collect(Collectors.groupingBy(Booking::getVisitDate));
        
        for (int day = 1; day <= daysInMonth; day++) {
        	LocalDate date = LocalDate.of(year, month, day);
            List<Booking> bookingList = bookingsByDate.getOrDefault(date, List.of());
            
        	int visitorCount = 0;
        	for (Booking booking : bookingList) {
        		visitorCount += booking.getGroupSize();
        	}

        	CalendarDay cDay = new CalendarDay();
        	cDay.setBookings(bookingList.size());
        	cDay.setVisitors(visitorCount);
       
        	calendar.put(day, cDay);
        }
		
		return calendar;
	}

	@Transactional(readOnly = true)
	public Integer getNumberOfBookingByMonth(LocalDate date) {
		Integer year = date.getYear();
		Integer month = date.getMonthValue();
		
		YearMonth yearMonth = YearMonth.of(year, month);
	    LocalDate startDate = yearMonth.atDay(1);
	    LocalDate endDate = yearMonth.atEndOfMonth();
		
		List<Booking> bookings = bookingRepository.findByVisitDateBetween(startDate, endDate);
		
		return bookings.size();
	}

	@Transactional(readOnly = true)
	public Long getMonthRevenue(LocalDate date) {
		Integer year = date.getYear();
		Integer month = date.getMonthValue();
		
		YearMonth yearMonth = YearMonth.of(year, month);
	    LocalDate startDate = yearMonth.atDay(1);
	    LocalDate endDate = yearMonth.atEndOfMonth();
		
	    return bookingRepository.getTotalRevenueByBookingStatusAndVisitDateBetween(startDate, endDate, BookingStatus.COMPLETED);
	}

	@Transactional(readOnly = true)
	public Integer getNumberOfPendingBookings() {
		return bookingRepository.countByBookingStatus(BookingStatus.PENDING).intValue();
	}

	@Transactional(readOnly = true)
	public List<ChartData> getYearlyRevenue(Integer startYear, Integer endYear) {
		if (startYear == null) {
			startYear = bookingRepository.getEarliestYear();
		}
		
		if (endYear == null) {
			endYear = bookingRepository.getLatestYear();
		}
		
		if (startYear > endYear) {
			throw new IllegalArgumentException("Starting year cannot be greater than the ending year");
		}
		
		List<ChartData> revenues = new ArrayList<>();
	    
	    for (int year = startYear; year <= endYear; year++) {
	    	LocalDate startDate = LocalDate.of(year, 1, 1);
	        LocalDate endDate = LocalDate.of(year, 12, 31);

	        Long totalRevenue = bookingRepository.getTotalRevenueByBookingStatusAndVisitDateBetween(
	            startDate, 
	            endDate, 
	            Booking.BookingStatus.COMPLETED
	        );
	        
	        revenues.add(
	        		new ChartData(
	        				String.valueOf(year),
	        				totalRevenue));
	    }
	    
	    return revenues;
	}

	@Transactional(readOnly = true)
	public List<ChartData> getMonthlyRevenue(Integer year) {
		if (year == null) {
			throw new IllegalArgumentException("Please provide a year.");
		}
		
		List<ChartData> revenues = new ArrayList<>();
	    
	    for (int month = 1; month <= 12; month++) {
	        YearMonth yearMonth = YearMonth.of(year, month);
	        LocalDate startDate = yearMonth.atDay(1);
	        LocalDate endDate = yearMonth.atEndOfMonth();
	        
	        Long totalRevenue = bookingRepository.getTotalRevenueByBookingStatusAndVisitDateBetween(startDate, endDate, Booking.BookingStatus.COMPLETED);
	        
	        revenues.add(
	        		new ChartData(
	        				yearMonth.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH), 
	        				totalRevenue));
	    }
	    
	    return revenues;
	}
	
	
	public Booking updateBooking(int id, UserBookingUpdateRequest request) {
		if (request.getVisitDate() == null 
				&& request.getVisitTime() == null
				&& request.getGroupSize() == null
				&& request.getBookingInclusionRequests() == null) {
			throw new IllegalArgumentException("No fields provided to update booking.");
		}
		
		Booking existingBooking = bookingRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Booking with ID '" + id + "' not found."));
		
		LocalDate visitDate = request.getVisitDate() != null ? request.getVisitDate() : null;
		LocalTime visitTime = request.getVisitTime() != null ? request.getVisitTime() : null;
		Integer groupSize = request.getGroupSize() != null ? request.getGroupSize() : null;
		List<BookingInclusionRequest> bookingInclusionRequests = request.getBookingInclusionRequests() != null 
				? request.getBookingInclusionRequests() : null;
		
		
		Booking prevBooking = createBookingCopy(existingBooking);
		
		
		boolean recalculatePrice = false;
		
		if (visitDate != null) {
			LocalDate today = LocalDate.now();
		    LocalDate currentBookingDate = existingBooking.getVisitDate();
	
		    // If the current booking date is within 2 days, forbid changes
		    if (!currentBookingDate.isAfter(today.plusDays(1))) {
		        throw new IllegalArgumentException("Cannot change the visit date because the booking is within 2 days.");
		    }
		  
			validateVisitDate(visitDate);
			existingBooking.setVisitDate(visitDate);
		}
		
		if (visitTime != null) {
			LocalDate today = LocalDate.now();
		    LocalDate currentBookingDate = existingBooking.getVisitDate();

		    // If the booking is within 2 days, forbid time changes
		    if (!currentBookingDate.isAfter(today.plusDays(1))) {
		        throw new IllegalArgumentException("Cannot change the visit time because the booking is within 2 days.");
		    }
		    
			validateVisitTime(existingBooking.getBookingId(), existingBooking.getTourPackage().getDuration(), visitDate, visitTime);
			existingBooking.setVisitTime(visitTime);
		}
		
		if (groupSize != null) {
			recalculatePrice = true;
			existingBooking.setGroupSize(groupSize);
		}
		
		if (bookingInclusionRequests != null) {
			recalculatePrice = true;
			
			// Remove old inclusions
		    existingBooking.getBookingInclusions().clear();

		    // Add new inclusions
		    List<BookingInclusion> newInclusions = new ArrayList<>();
		    for (BookingInclusionRequest reqInclusion : bookingInclusionRequests) {
		        PackageInclusion inclusion = inclusionRepository.findById(reqInclusion.getInclusionId())
		                .orElseThrow(() -> new IllegalArgumentException("Inclusion ID " + reqInclusion.getInclusionId() + " not found."));

		        newInclusions.add(BookingInclusion.builder()
		                .booking(existingBooking)
		                .inclusion(inclusion)
		                .quantity(reqInclusion.getQuantity())
		                .priceAtBooking(inclusion.getInclusionPricePerPerson())
		                .build());
		    }
		    
		    existingBooking.setBookingInclusions(newInclusions);
		}
		
		if (recalculatePrice) {
			existingBooking.setTotalPrice(
				(existingBooking.getTourPackage().getBasePrice() * existingBooking.getGroupSize()) +
				getTotalInclusionPrice(existingBooking)
			);
		}
		
		logIfStaffOrAdmin("Booking", (long) id, LogAction.UPDATE, prevBooking, existingBooking);
		
		return bookingRepository.save(existingBooking);
	}
	
	public Booking updateBookingByAdmin(int id, BookingUpdateRequest request) {
		if (request.getVisitDate() == null 
				&& request.getVisitTime() == null
				&& request.getGroupSize() == null
				&& request.getBookingInclusionRequests() == null
				&& request.getBookingStatus() == null
				&& request.getPaymentStatus() == null) {
			throw new IllegalArgumentException("No fields provided to update booking.");
		}
		
		Booking existingBooking = bookingRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Booking with ID '" + id + "' not found."));
		
		LocalDate visitDate = request.getVisitDate() != null ? request.getVisitDate() : null;
		LocalTime visitTime = request.getVisitTime() != null ? request.getVisitTime() : null;
		Integer groupSize = request.getGroupSize() != null ? request.getGroupSize() : null;
		List<BookingInclusionRequest> bookingInclusionRequests = request.getBookingInclusionRequests() != null 
				? request.getBookingInclusionRequests() : null;
		
		BookingStatus bookingStatus = request.getBookingStatus() != null ? request.getBookingStatus() : null;
		PaymentStatus paymentStatus = request.getPaymentStatus() != null ? request.getPaymentStatus() : null;
		
		Booking prevBooking = createBookingCopy(existingBooking);
		
		boolean recalculatePrice = false;
		
		if (visitDate != null) {
			LocalDate today = LocalDate.now();
		    LocalDate currentBookingDate = existingBooking.getVisitDate();
	
		    // If the current booking date is within 2 days, forbid changes
		    if (!currentBookingDate.isAfter(today.plusDays(1))) {
		        throw new IllegalArgumentException("Cannot change the visit date because the booking is within 2 days.");
		    }
		  
			validateVisitDate(visitDate);
			existingBooking.setVisitDate(visitDate);
		}
		
		if (visitTime != null) {
			 LocalDate today = LocalDate.now();
		    LocalDate currentBookingDate = existingBooking.getVisitDate();

		    // If the booking is within 2 days, forbid time changes
		    if (!currentBookingDate.isAfter(today.plusDays(1))) {
		        throw new IllegalArgumentException("Cannot change the visit time because the booking is within 2 days.");
		    }
		    
			validateVisitTime(existingBooking.getBookingId(), existingBooking.getTourPackage().getDuration(), visitDate, visitTime);
			existingBooking.setVisitTime(visitTime);
		}
		
		if (groupSize != null) {
			recalculatePrice = true;
			existingBooking.setGroupSize(groupSize);
		}
		
		if (bookingInclusionRequests != null) {
			recalculatePrice = true;
			
			// Remove old inclusions
		    existingBooking.getBookingInclusions().clear();

		    // Add new inclusions
		    List<BookingInclusion> newInclusions = new ArrayList<>();
		    for (BookingInclusionRequest reqInclusion : bookingInclusionRequests) {
		        PackageInclusion inclusion = inclusionRepository.findById(reqInclusion.getInclusionId())
		                .orElseThrow(() -> new IllegalArgumentException("Inclusion ID " + reqInclusion.getInclusionId() + " not found."));

		        newInclusions.add(BookingInclusion.builder()
		                .booking(existingBooking)
		                .inclusion(inclusion)
		                .quantity(reqInclusion.getQuantity())
		                .priceAtBooking(inclusion.getInclusionPricePerPerson())
		                .build());
		    }
		    
		    existingBooking.setBookingInclusions(newInclusions);
		}
		
		if (recalculatePrice) {
			existingBooking.setTotalPrice(
				(existingBooking.getTourPackage().getBasePrice() * existingBooking.getGroupSize()) +
				getTotalInclusionPrice(existingBooking)
			);
		}
		
		if (bookingStatus != null) {
		    if (!existingBooking.getBookingStatus().canTransitionTo(bookingStatus)) {
		        throw new IllegalArgumentException(
		            "Cannot change booking status from " + existingBooking.getBookingStatus() + 
		            " to " + bookingStatus
		        );
		    }
		    
		    existingBooking.setBookingStatus(bookingStatus);
		}

		if (paymentStatus != null) {
		    if (!existingBooking.getPaymentStatus().canTransitionTo(paymentStatus)) {
		        throw new IllegalArgumentException(
		            "Cannot change payment status from " + existingBooking.getPaymentStatus() + 
		            " to " + paymentStatus
		        );
		    }
		    
		    existingBooking.setPaymentStatus(paymentStatus);
		}
		
		logIfStaffOrAdmin("Booking", (long) id, LogAction.UPDATE, prevBooking, existingBooking);
		
		return bookingRepository.save(existingBooking);
	}
	
	public void softDeleteBooking(int id) {
		Booking booking = bookingRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Booking with ID '" + id + "' not found."));
	    
		if (!booking.isActive()) {
	        throw new IllegalStateException("Booking is already disabled.");
	    }
	    
	    Booking prevBooking = createBookingCopy(booking);

	    booking.setActive(false);
		bookingRepository.save(booking);
		
		logIfStaffOrAdmin("Booking", (long) id, LogAction.DISABLE, prevBooking, booking);
	}
	
	public void restoreBooking(int id) {
		Booking booking = bookingRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Booking with ID '" + id + "' not found."));
	    
		if (booking.isActive()) {
	        throw new IllegalStateException("Booking is already active.");
	    }
	    
	    Booking prevBooking = createBookingCopy(booking);

	    booking.setActive(true);
		bookingRepository.save(booking);
		
		logIfStaffOrAdmin("Booking", (long) id, LogAction.RESTORE, prevBooking, booking);
	}
}
 