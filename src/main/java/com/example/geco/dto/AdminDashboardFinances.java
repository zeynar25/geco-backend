package com.example.geco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardFinances {
	Long totalRevenue;
	Long averageRevenuePerBooking;
	Integer totalBookings;
	Integer completedBookings;
}
