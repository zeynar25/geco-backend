package com.example.geco.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.geco.domains.Account;
import com.example.geco.domains.Booking;
import com.example.geco.domains.Booking.BookingStatus;
import com.example.geco.domains.BookingInclusion;
import com.example.geco.dto.AdminBookingRequest;
import com.example.geco.dto.AdminDashboardFinances;
import com.example.geco.dto.CalendarDay;
import com.example.geco.dto.BookingRevenue;
import com.example.geco.repositories.AccountRepository;
import com.example.geco.repositories.BookingRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BookingService {
	@Autowired
	private BookingRepository bookingRepository;
	
	private void checkVisitDate(Booking booking) {
		LocalDate today = LocalDate.now();
		
		if (!booking.getVisitDate().isAfter(today.plusDays(1))) {
		    throw new IllegalArgumentException("Booking visit date must be at least 2 days from today.");
		}
	}
	
	private void checkVisitTime(Booking booking) {
		int tourDurationMinutes = booking.getTourPackage().getDuration() != null
                ? booking.getTourPackage().getDuration()
                : 0;

		// Park opens by 8:00, giving time to prepare for the tour by 9:00.
		LocalTime startTime = LocalTime.of(9, 0);
		
		// Ensure tour finishes by 16:00, giving time to close by 17:00
		LocalTime endTime = LocalTime.of(16, 0).minusMinutes(tourDurationMinutes); 
		
		if (booking.getVisitTime().isBefore(startTime) || booking.getVisitTime().isAfter(endTime)) {
			throw new IllegalArgumentException("Booking visit time must be between 9:00 and end at by 16:00.");
		}
		
	    // Check if there's an overlap in schedule and selected schedule.
		checkScheduleOverlap(booking.getVisitDate(), 
				booking.getVisitTime(),
				booking.getVisitTime().plusMinutes(tourDurationMinutes)
		);
	}
	
	private void checkScheduleOverlap(LocalDate visitDate, LocalTime requestedStart, LocalTime requestedEnd) {
		List<Booking> bookingsOnDate = bookingRepository.findByVisitDateOrderByVisitTimeAsc(visitDate);
				
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
	
	private int getTotalInclusionPrice(Booking booking) {
		int price = 0;
		for (BookingInclusion inclusion : booking.getInclusions()) {
	    	if (inclusion.getPriceAtBooking() == null || inclusion.getQuantity() == null) {
	            throw new IllegalArgumentException("Booking Inclusion must have a price and quantity.");
	        }
	        if (inclusion.getQuantity() > booking.getGroupSize()) {
	            throw new IllegalArgumentException("Inclusion quantity cannot exceed booking group size.");
	        }
	        
	    	price += inclusion.getPriceAtBooking() * inclusion.getQuantity();
	    }
		
		return price;
	}
	
	public Booking addBooking(Booking booking) {
		if (booking.getAccount() == null) {
	        throw new IllegalArgumentException("Booking's account is missing.");
	    }
		
		if (booking.getTourPackage() == null) {
	        throw new IllegalArgumentException("Booking's Tour Package is missing.");
	    }
		
		if (booking.getInclusions() == null) {
			booking.setInclusions(new ArrayList<>());
	    }
		
		if (booking.getVisitDate() == null) {
	        throw new IllegalArgumentException("Booking's visit date is missing.");
		}
		
		if (booking.getVisitTime() == null) {
	        throw new IllegalArgumentException("Booking's visit time is missing.");
		}
		
		checkVisitDate(booking);
		checkVisitTime(booking);
		
		if (booking.getGroupSize() == null) {
	        throw new IllegalArgumentException("Booking's group size is missing.");
	    }
		
		if (booking.getGroupSize() <= 0) {
	        throw new IllegalArgumentException("Invalid Booking's group size.");
	    }
		
	    booking.setStatus(BookingStatus.PENDING);
	    booking.setTotalPrice((booking.getTourPackage().getBasePrice() * booking.getGroupSize()) + getTotalInclusionPrice(booking));
		
		return bookingRepository.save(booking);
	}
	
	public Booking getBooking(int id) {
		return bookingRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Booking with ID \"" + id + "\" not found."));
	}
	
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
	
	public Booking updateBooking(Booking booking) {
		if (booking.getAccount() == null &&
				booking.getTourPackage() == null &&
				booking.getVisitDate() == null &&
				booking.getVisitTime() == null &&
				booking.getGroupSize() == null &&
				booking.getStatus() == null) {
	        throw new IllegalArgumentException("No fields provided to update for Booking.");
		}
		
		Booking existingBooking = bookingRepository.findById(booking.getBookingId())
	            .orElseThrow(() -> new EntityNotFoundException("Booking with ID \"" + booking.getBookingId() + "\" not found."));
		
		if (booking.getAccount() != null) {
			existingBooking.setAccount(booking.getAccount());
	    }
		
		boolean recalculatePrice = false;
		
		if (booking.getTourPackage() != null) {
			existingBooking.setTourPackage(booking.getTourPackage());
			recalculatePrice = true;
	    }
		
//		if (booking.getInclusions() != null) {
//			existingBooking.setInclusions(booking.getInclusions());
//			recalculatePrice = true;
//		}
		
		if (booking.getVisitDate() != null) {
			// Should add a condition that if existingBooking.getVisitDate is 2 days from today, we can't change dates.
			checkVisitDate(booking);
			existingBooking.setVisitDate(booking.getVisitDate());
		}
		
		if (booking.getVisitTime() != null) {
			// Should add a condition that if existingBooking.getVisitDate is 2 days from today, we can't change time.
			
			checkVisitTime(booking);
			existingBooking.setVisitTime(booking.getVisitTime());
		}
		
		if (booking.getGroupSize() != null) {
			if (booking.getGroupSize() <= 0) {
		        throw new IllegalArgumentException("Invalid Booking's group size.");
		    }
			
			recalculatePrice = true;
			existingBooking.setGroupSize(booking.getGroupSize());
		}
		
		if (booking.getStatus() != null) {
			existingBooking.setStatus(booking.getStatus());
		}
		
		if (recalculatePrice) {
			existingBooking.setTotalPrice(
				(existingBooking.getTourPackage().getBasePrice() * existingBooking.getGroupSize()) +
				getTotalInclusionPrice(existingBooking)
			);
		}
		
		return bookingRepository.save(existingBooking);
	}
	
	public Booking deleteBooking(int id) {
		Booking booking = bookingRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Booking with ID \"" + id + "\" not found."));
	    
		bookingRepository.delete(booking);
		
	    return booking;
	}
	
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
	
	public Integer getNumberOfBookingByMonth(LocalDate date) {
		Integer year = date.getYear();
		Integer month = date.getMonthValue();
		
		YearMonth yearMonth = YearMonth.of(year, month);
	    LocalDate startDate = yearMonth.atDay(1);
	    LocalDate endDate = yearMonth.atEndOfMonth();
		
		List<Booking> bookings = bookingRepository.findByVisitDateBetween(startDate, endDate);
		
		return bookings.size();
	}

	public Long getMonthRevenue(LocalDate date) {
		Integer year = date.getYear();
		Integer month = date.getMonthValue();
		
		YearMonth yearMonth = YearMonth.of(year, month);
	    LocalDate startDate = yearMonth.atDay(1);
	    LocalDate endDate = yearMonth.atEndOfMonth();
		
	    return bookingRepository.getRevenue(startDate, endDate, BookingStatus.COMPLETED);
	}

	public Integer getNumberOfPendingBookings() {
		return bookingRepository.countByStatus(BookingStatus.PENDING).intValue();
	}

	public List<BookingRevenue> getYearlyRevenue(Integer startYear, Integer endYear) {
		if (startYear == null) {
			startYear = bookingRepository.getEarliestYear();
		}
		
		if (endYear == null) {
			endYear = bookingRepository.getLatestYear();
		}
		
		if (startYear > endYear) {
			throw new IllegalArgumentException("Starting year cannot be greater than the ending year");
		}
		
		List<BookingRevenue> revenues = new ArrayList<>();
	    
	    for (int year = startYear; year <= endYear; year++) {
	    	LocalDate startDate = LocalDate.of(year, 1, 1);
	        LocalDate endDate = LocalDate.of(year, 12, 31);

	        Long totalRevenue = bookingRepository.getRevenue(
	            startDate, 
	            endDate, 
	            Booking.BookingStatus.COMPLETED
	        );
	        
	        revenues.add(
	        		new BookingRevenue(
	        				String.valueOf(year),
	        				totalRevenue));
	    }
	    
	    return revenues;
	}

	public List<BookingRevenue> getMonthlyRevenue(Integer year) {
		if (year == null) {
			throw new IllegalArgumentException("Please provide a year.");
		}
		
		List<BookingRevenue> revenues = new ArrayList<>();
	    
	    for (int month = 1; month <= 12; month++) {
	        YearMonth yearMonth = YearMonth.of(year, month);
	        LocalDate startDate = yearMonth.atDay(1);
	        LocalDate endDate = yearMonth.atEndOfMonth();
	        
	        Long totalRevenue = bookingRepository.getRevenue(startDate, endDate, Booking.BookingStatus.COMPLETED);
	        
	        revenues.add(
	        		new BookingRevenue(
	        				yearMonth.getMonth().name(), 
	        				totalRevenue));
	    }
	    
	    return revenues;
	}
}
 