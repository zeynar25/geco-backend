package com.example.geco.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.geco.domains.AuditLog.LogAction;
import com.example.geco.domains.Booking.BookingStatus;
import com.example.geco.domains.CalendarDate;
import com.example.geco.domains.CalendarDate.DateStatus;
import com.example.geco.domains.Restriction;
import com.example.geco.dto.CalendarDateRequest;
import com.example.geco.repositories.BookingRepository;
import com.example.geco.repositories.CalendarDateRepository;
import com.example.geco.repositories.RestrictionRepository;

@Service
@Transactional
public class CalendarDateService extends BaseService {
	@Autowired
	public BookingRepository bookingRepository;
	
	@Autowired
    public RestrictionRepository restrictionRepository;

	
	@Autowired
	public CalendarDateRepository calendarDateRepository;
	
	public CalendarDate createCalendarDateCopy(CalendarDate c) {
		return CalendarDate.builder()
				.dateId(c.getDateId())
				.date(c.getDate())
				.dateStatus(c.getDateStatus())
				.bookingLimit(c.getBookingLimit())
			    .build();
	}
	
	private Integer getGlobalBookingLimitOrNull() {
        return restrictionRepository.findByNameIgnoreCase("booking_limit")
            .map(Restriction::getValue) // assumes Integer
            .orElse(null);
    }
	
	@Transactional(readOnly = true)
	public List<CalendarDate> getCalendarDateByYearMonth(
			DateStatus status,
			YearMonth yearMonth) {
		LocalDate startDate = yearMonth.atDay(1);
	    LocalDate endDate = yearMonth.atEndOfMonth();
		
		if (status == null) {
		    return calendarDateRepository.findByDateBetweenOrderByDate(startDate, endDate);
		}
		
	    return calendarDateRepository.findByDateStatusAndDateBetweenOrderByDate(status, startDate, endDate);
	}
	
	@Transactional
    public void refreshCalendarDateStatus(LocalDate date) {
        CalendarDate cd = calendarDateRepository.findByDate(date)
            .orElseGet(() -> calendarDateRepository.save(
                CalendarDate.builder()
                    .date(date)
                    .dateStatus(DateStatus.AVAILABLE)
                    .bookingLimit(null) // inherit global by default
                    .build()
            ));

        if (cd.getDateStatus() == DateStatus.CLOSED) return;

        Integer effectiveLimit = (cd.getBookingLimit() != null)
            ? cd.getBookingLimit()
            : getGlobalBookingLimitOrNull();

        if (effectiveLimit == null) return;

        long acceptedCount = bookingRepository.countByVisitDateAndBookingStatusIn(
            date,
            List.of(BookingStatus.APPROVED, BookingStatus.COMPLETED)
        );

        cd.setDateStatus(acceptedCount >= effectiveLimit ? DateStatus.FULLY_BOOKED : DateStatus.AVAILABLE);
        calendarDateRepository.save(cd);
    }
	

	public CalendarDate updateCalendarDate(CalendarDateRequest request) {
	    if (request == null) {
	        throw new IllegalArgumentException("CalendarDate request is missing.");
	    }
	    if (request.getDate() == null) {
	        throw new IllegalArgumentException("CalendarDate date is missing.");
	    }
	    if (request.getDateStatus() == null) {
	        throw new IllegalArgumentException("CalendarDate dateStatus is missing.");
	    }

	    Optional<CalendarDate> byDate = calendarDateRepository.findByDate(request.getDate());

	    CalendarDate entity;
	    CalendarDate prev = null;

	    if (byDate.isPresent()) {
	        entity = byDate.get();
	        prev = createCalendarDateCopy(entity);

	        entity.setDate(request.getDate());
	        entity.setDateStatus(request.getDateStatus());

	        if (request.getBookingLimit() != null) {
	            entity.setBookingLimit(request.getBookingLimit());
	        }
	    } else {
	        entity = CalendarDate.builder()
	            .date(request.getDate())
	            .dateStatus(request.getDateStatus())
	            .bookingLimit(request.getBookingLimit())
	            .build();
	    }

	    // --- Auto status update based on accepted bookings vs booking limit ---
	    // Don't override CLOSED days.

    	
   	 	List<BookingStatus> activeStatuses = 
   			 List.of(BookingStatus.APPROVED, BookingStatus.COMPLETED);
	        	
   	 
	    if (entity.getDateStatus() != DateStatus.CLOSED) {
	        Integer effectiveLimit = entity.getBookingLimit(); // per-date limit (already includes request update if provided)

	        if (effectiveLimit != null) {
	        	long acceptedCount =
        		    bookingRepository.countByVisitDateAndBookingStatusIn(
        		        entity.getDate(),
        		        activeStatuses
        		    );
	        	
	        	if (acceptedCount >= effectiveLimit) {
	        	    entity.setDateStatus(DateStatus.FULLY_BOOKED);
	        	} else {
	        	    entity.setDateStatus(DateStatus.AVAILABLE);
	        	}
	        }
	        // If effectiveLimit is null: do nothing here (your calendar endpoint can fall back to global).
	    }

	    CalendarDate saved = calendarDateRepository.save(entity);

	    if (byDate.isPresent()) {
	        logIfStaffOrAdmin("CalendarDate", (long) saved.getDateId(), LogAction.UPDATE, prev, saved);
	    } else {
	        logIfStaffOrAdmin("CalendarDate", (long) saved.getDateId(), LogAction.CREATE, null, saved);
	    }

	    return saved;
	}
	
	@Transactional
	public void updateCalendarDateBookingLimit(LocalDate fromDate, Integer oldValue, Integer newValue) {
	    if (fromDate == null) {
	        fromDate = LocalDate.now();
	    }
	    if (newValue == null) {
	        throw new IllegalArgumentException("New booking limit is missing.");
	    }
	    if (newValue < 0) {
	        throw new IllegalArgumentException("New booking limit must be 0 or greater.");
	    }

	    List<CalendarDate> datesToUpdate =
	        (oldValue == null)
	            ? calendarDateRepository.findByDateGreaterThanEqual(fromDate)
	            : calendarDateRepository.findByDateGreaterThanEqualAndBookingLimit(fromDate, oldValue);

	    // Define which bookings count toward capacity
	    List<BookingStatus> activeStatuses = List.of(
	        BookingStatus.APPROVED
	    );

	    // Count active bookings per date in ONE query
	    Map<LocalDate, Long> activeBookingsByDate =
	        bookingRepository.countBookingsByVisitDateFromAndStatusIn(fromDate, activeStatuses)
	            .stream()
	            .collect(Collectors.toMap(
	                row -> (LocalDate) row[0],
	                row -> (Long) row[1]
	            ));

	    for (CalendarDate calendarDate : datesToUpdate) {
	        calendarDate.setBookingLimit(newValue);

	        // Don’t override CLOSED
	        if (calendarDate.getDateStatus() == DateStatus.CLOSED) {
	            continue;
	        }

	        long activeBookings = activeBookingsByDate.getOrDefault(calendarDate.getDate(), 0L);

	        if (activeBookings >= newValue) {
	            calendarDate.setDateStatus(DateStatus.FULLY_BOOKED);
	        } else {
	            calendarDate.setDateStatus(DateStatus.AVAILABLE);
	        }
	    }

	    calendarDateRepository.saveAll(datesToUpdate);
	}
}
