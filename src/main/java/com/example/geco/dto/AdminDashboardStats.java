package com.example.geco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStats {
	Integer monthlyBooking;
	Integer monthlyRevenue;
	Integer pendingBookings;
	Integer unreadFeedback;
}
