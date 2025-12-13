package com.example.geco.dto;

import java.time.LocalDate;

import com.example.geco.domains.CalendarDate.DateStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarDateRequest {
	@NotNull(message = "CalendarDate's date is missing.")
	private LocalDate date;
	
	@NotNull(message = "CalendarDate's dateStatus is missing.")
	private DateStatus dateStatus;
}
