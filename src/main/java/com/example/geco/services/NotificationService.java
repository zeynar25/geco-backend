package com.example.geco.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.geco.domains.Account;
import com.example.geco.domains.Booking;
import com.example.geco.domains.Notification;
import com.example.geco.repositories.NotificationRepository;

@Service
@Transactional
public class NotificationService {
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	public Notification addNotification(
			Account account,
			Booking booking,
			String message) {
		Notification notif = Notification.builder()
				.account(account)
				.booking(booking)
				.read(false)
				.message(message)
				.build();
		
		return notificationRepository.save(notif);
	}

}
