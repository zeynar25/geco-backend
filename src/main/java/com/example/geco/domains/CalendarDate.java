package com.example.geco.domains;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="calendar_date")
public class CalendarDate {
	public enum DateStatus {
	    AVAILABLE, FULLY_BOOKED, CLOSED
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Integer dateId;
	
	@Column(nullable = false, unique = true)
	private LocalDate date;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DateStatus dateStatus;
}
