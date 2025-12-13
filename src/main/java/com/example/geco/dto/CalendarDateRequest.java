package com.example.geco.dto;

import java.util.Date;

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
	private Date date;
	
	@NotNull(message = "CalendarDate's dateStatus is missing.")
	private DateStatus dateStatus;
}
