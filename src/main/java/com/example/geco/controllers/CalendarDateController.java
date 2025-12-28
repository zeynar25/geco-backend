package com.example.geco.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.CalendarDate;
import com.example.geco.dto.CalendarDateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/calendar-date")
@Tag(name = "Calendar Date", description = "Operations about CalendarDate")
public class CalendarDateController extends AbstractController{
	@PostMapping
    @Operation(
        summary = "Create a new date",
        description = "Adds a new date with a given status"
    )
    public ResponseEntity<CalendarDate> addCalendarDate(
            @RequestBody CalendarDateRequest request) {
        CalendarDate savedDate = calendarDateService.addCalendarDate(request);
        return new ResponseEntity<>(savedDate, HttpStatus.CREATED);
    }
	
	@PutMapping
    @Operation(
        summary = "Updates an existing CalendarDate object",
        description = "Updates an existing CalendarDate with the status provided"
    )
    public ResponseEntity<CalendarDate> updateCalendarDate(@PathVariable int id,
            @RequestBody @Valid CalendarDateRequest request) {
		CalendarDate savedDate = calendarDateService.updateCalendarDate(id, request);
        return new ResponseEntity<>(savedDate, HttpStatus.OK);
    }
}
