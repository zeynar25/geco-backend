package com.example.geco.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.geco.domains.AuditLog.LogAction;
import com.example.geco.domains.CalendarDate;
import com.example.geco.domains.CalendarDate.DateStatus;
import com.example.geco.dto.CalendarDateRequest;
import com.example.geco.repositories.CalendarDateRepository;

@Service
@Transactional
public class CalendarDateService extends BaseService {
	@Autowired
	public CalendarDateRepository calendarDateRepository;
	
	public CalendarDate createCalendarDateCopy(CalendarDate c) {
		return CalendarDate.builder()
				.dateId(c.getDateId())
				.date(c.getDate())
				.dateStatus(c.getDateStatus())
			    .build();
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
	

	public CalendarDate updateCalendarDate(CalendarDateRequest request) {
	    if (request == null) {
	        throw new IllegalArgumentException("CalendarDate request is missing.");
	    }

	    Optional<CalendarDate> byDate = calendarDateRepository.findByDate(request.getDate());

	    if (byDate.isPresent()) {
	        CalendarDate existing = byDate.get();
	        CalendarDate prev = createCalendarDateCopy(existing);

	        existing.setDate(request.getDate());
	        existing.setDateStatus(request.getDateStatus());

	        if (request.getBookingLimit() != null) {
	            existing.setBookingLimit(request.getBookingLimit());
	        }

	        CalendarDate saved = calendarDateRepository.save(existing);
	        logIfStaffOrAdmin("CalendarDate", (long) saved.getDateId(), LogAction.UPDATE, prev, saved);
	        return saved;
	    } else {
	        CalendarDate cd = CalendarDate.builder()
	            .date(request.getDate())
	            .dateStatus(request.getDateStatus())
	            .bookingLimit(request.getBookingLimit())
	            .build();

	        CalendarDate saved = calendarDateRepository.save(cd);
	        logIfStaffOrAdmin("CalendarDate", (long) saved.getDateId(), LogAction.CREATE, null, saved);
	        return saved;
	    }
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

	    // SAFEST: only overwrite rows that still match the old limit
	    List<CalendarDate> datesToUpdate =
	        (oldValue == null)
	            ? calendarDateRepository.findByDateGreaterThanEqual(fromDate)
	            : calendarDateRepository.findByDateGreaterThanEqualAndBookingLimit(fromDate, oldValue);

	    for (CalendarDate calendarDate : datesToUpdate) {
	        calendarDate.setBookingLimit(newValue);
	    }

	    calendarDateRepository.saveAll(datesToUpdate);
	}
}
