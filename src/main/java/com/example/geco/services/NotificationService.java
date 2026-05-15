package com.example.geco.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.geco.domains.Account;
import com.example.geco.domains.Booking;
import com.example.geco.domains.Notification;
import com.example.geco.repositories.NotificationRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class NotificationService extends BaseService {
    
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
    
    @Transactional(readOnly = true)
    public Page<Notification> getMyLatestNotifications(int limit) {
        int accountId = getLoggedAccountId();
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return notificationRepository.findByAccount_AccountIdAndIsActive(accountId, true, pageable);
    }
    
    private LocalDateTime startOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    private LocalDateTime endOfDay(LocalDate date) {
        return date == null ? null : date.atTime(LocalTime.MAX);
    }

    @Transactional(readOnly = true)
    public Page<Notification> getMyNotifications(
            Boolean isRead,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size) {
        int accountId = getLoggedAccountId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        LocalDateTime start = startOfDay(startDate);
        LocalDateTime end = endOfDay(endDate);

        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be earlier than start date.");
        }

        return notificationRepository.searchMyNotifications(
            accountId, isRead, start, end, true, pageable
        );
    }
    
    public Notification markAsRead(int notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notification with ID '" + notificationId + "' not found."));
        
        int loggedAccountId = getLoggedAccountId();
        if (notification.getAccount().getAccountId() != loggedAccountId) {
            throw new IllegalArgumentException("You cannot mark other users' notifications as read.");
        }
        
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
    
    public Notification markAsUnread(int notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notification with ID '" + notificationId + "' not found."));
        
        int loggedAccountId = getLoggedAccountId();
        if (notification.getAccount().getAccountId() != loggedAccountId) {
            throw new IllegalArgumentException("You cannot mark other users' notifications as unread.");
        }
        
        notification.setRead(false);
        return notificationRepository.save(notification);
    }
    
    public void deleteNotification(int notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notification with ID '" + notificationId + "' not found."));
        
        int loggedAccountId = getLoggedAccountId();
        if (notification.getAccount().getAccountId() != loggedAccountId) {
            throw new IllegalArgumentException("You cannot delete other users' notifications.");
        }
        
        notificationRepository.deleteById(notificationId);
    }
    
    public void markAllAsRead() {
        int accountId = getLoggedAccountId();
        notificationRepository.markAllAsReadByAccountId(accountId);
    }

}