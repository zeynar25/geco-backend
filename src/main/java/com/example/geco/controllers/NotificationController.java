package com.example.geco.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.Notification;
import com.example.geco.services.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/notification")
@Tag(
    name = "Notification Controller",
    description = "Manage user notifications including retrieval, marking as read/unread, and deletion"
)
public class NotificationController extends AbstractController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Operation(
        summary = "Get Latest Notifications",
        description = "Retrieve the latest N notifications for the logged-in user. Useful for displaying notifications in a dropdown (e.g., latest 5)."
    )
    @GetMapping("/latest")
    public ResponseEntity<Page<Notification>> getLatestNotifications(
        @Parameter(description = "Number of latest notifications to retrieve", example = "5")
        @RequestParam(defaultValue = "5") int limit
    ) {
        Page<Notification> notifications = notificationService.getMyLatestNotifications(limit);
        return ResponseEntity.ok(notifications);
    }
    
    @Operation(
	    summary = "Get All My Notifications",
	    description = "Retrieve all notifications for the logged-in user with pagination. Can optionally filter by read status and date range."
	)
	@GetMapping
	public ResponseEntity<Page<Notification>> getMyNotifications(
	    @Parameter(description = "Filter by read status (true/false). If not provided, returns all notifications.")
	    @RequestParam(required = false) Boolean isRead,

	    @Parameter(description = "Start date for filtering notifications (ISO date)")
	    @RequestParam(required = false)
	    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

	    @Parameter(description = "End date for filtering notifications (ISO date)")
	    @RequestParam(required = false)
	    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

	    @Parameter(description = "Page number (0-based index)", example = "0")
	    @RequestParam(defaultValue = "0") int page,

	    @Parameter(description = "Number of records per page", example = "10")
	    @RequestParam(defaultValue = "10") int size
	) {
	    Page<Notification> notifications =
	        notificationService.getMyNotifications(isRead, startDate, endDate, page, size);
	    return ResponseEntity.ok(notifications);
	}
    
    @Operation(
        summary = "Mark Notification as Read",
        description = "Marks a specific notification as read."
    )
    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(
        @Parameter(description = "ID of the notification to mark as read")
        @PathVariable int id
    ) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(notification);
    }
    
    @Operation(
        summary = "Mark Notification as Unread",
        description = "Marks a specific notification as unread."
    )
    @PatchMapping("/{id}/unread")
    public ResponseEntity<Notification> markAsUnread(
        @Parameter(description = "ID of the notification to mark as unread")
        @PathVariable int id
    ) {
        Notification notification = notificationService.markAsUnread(id);
        return ResponseEntity.ok(notification);
    }
    
    @Operation(
        summary = "Delete Notification",
        description = "Deletes a specific notification."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
        @Parameter(description = "ID of the notification to delete")
        @PathVariable int id
    ) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(
        summary = "Mark All Notifications as Read",
        description = "Marks all notifications for the logged-in user as read."
    )
    @PatchMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.noContent().build();
    }

}