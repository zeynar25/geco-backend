package com.example.geco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardFinances {
	Long totalRevenue;
	Long averageRevenuePerBooking;
	Integer totalBookings;
	Integer conmpletedBookings;
}
