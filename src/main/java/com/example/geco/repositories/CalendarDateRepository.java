package com.example.geco.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.CalendarDate;
import com.example.geco.domains.CalendarDate.DateStatus;

@Repository
public interface CalendarDateRepository extends JpaRepository<CalendarDate, Integer>{
	Optional<CalendarDate> findByDate(LocalDate date);
	
	List<CalendarDate> findByDateBetweenOrderByDate(
			LocalDate startDate, LocalDate endDate
	);
	
	List<CalendarDate> findByDateStatusAndDateBetweenOrderByDate(
			DateStatus status, LocalDate startDate, LocalDate endDate
	);
}
