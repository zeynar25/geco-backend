package com.example.geco.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.CalendarDate;

@Repository
public interface CalendarDateRepository extends JpaRepository<CalendarDate, Integer>{

}
