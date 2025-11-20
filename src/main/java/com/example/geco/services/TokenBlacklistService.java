package com.example.geco.services;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

    private final ConcurrentMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();
    
    @Autowired
    private JwtService jwtService;

    public void blacklist(String token) {
        long expiryMillis = jwtService.getExpiryMillis(token);
        
        blacklistedTokens.put(token, expiryMillis);
    }

    // Check if token is blacklisted and remove expired ones.
    public boolean isBlacklisted(String token) {
        cleanExpiredTokens();
        return blacklistedTokens.containsKey(token);
    }

    // Remove expired tokens from the blacklist
    private void cleanExpiredTokens() {
        long now = System.currentTimeMillis();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue() <= now);
    }
}
