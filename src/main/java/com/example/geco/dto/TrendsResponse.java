package com.example.geco.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendsResponse {
	private List<ChartData> bookings;
    private List<ChartData> visitors;
    private List<ChartData> packages;
}
