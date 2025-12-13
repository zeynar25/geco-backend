package com.example.geco.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.geco.domains.AuditLog.LogAction;
import com.example.geco.domains.CalendarDate;
import com.example.geco.domains.CalendarDate.DateStatus;
import com.example.geco.dto.CalendarDateRequest;
import com.example.geco.repositories.CalendarDateRepository;

import jakarta.persistence.EntityNotFoundException;

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
	
	public CalendarDate addCalendarDate(CalendarDateRequest request) {
		CalendarDate cd = calendarDateRepository.save(
				CalendarDate.builder()
					.date(request.getDate())
					.dateStatus(request.getDateStatus())
					.build());
		
		logIfStaffOrAdmin("CalendarDate", (long) cd.getDateId(), LogAction.CREATE, null, cd);
		
	    return cd;
	}
	

	public CalendarDate updateCalendarDate(int id, CalendarDateRequest request) {
		CalendarDate existingCd = calendarDateRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Calendar Date with ID '"+ id + "' not found."));

		CalendarDate prevCd = createCalendarDateCopy(existingCd);
		existingCd.setDateStatus(request.getDateStatus());
		calendarDateRepository.save(existingCd);
		
		logIfStaffOrAdmin("CalendarDate", (long) existingCd.getDateId(), LogAction.UPDATE, prevCd, existingCd);
		
	    return existingCd;
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
}
