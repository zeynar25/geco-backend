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
    @Operation(
        summary = "Creates or Updates an existing CalendarDate object",
        description = "Creates or Updates an existing CalendarDate with the status provided"
    )
    @PostMapping
    public ResponseEntity<CalendarDate> updateCalendarDate(
            @RequestBody @Valid CalendarDateRequest request) {
		CalendarDate savedDate = calendarDateService.updateCalendarDate(request);
        return new ResponseEntity<>(savedDate, HttpStatus.OK);
    }
}
