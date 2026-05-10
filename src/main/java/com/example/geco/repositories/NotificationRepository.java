package com.example.geco.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer>{

}
