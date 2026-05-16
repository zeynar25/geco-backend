package com.example.geco.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.PaymentSettings;

@Repository
public interface PaymentSettingsRepository extends JpaRepository<PaymentSettings, Integer> {
    Optional<PaymentSettings> findFirstByIsActiveTrueOrderByUpdatedAtDesc();
}