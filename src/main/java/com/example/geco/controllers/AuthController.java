package com.example.geco.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.Account;
import com.example.geco.dto.LoginRequest;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/account")
public class AuthController extends AbstractController{

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
	    try {
	        String token = authService.verify(request);
	        return ResponseEntity.ok(token);
	    } catch (AuthenticationException | IllegalArgumentException e) {
	        // Catch both authentication and manual invalid credentials
	        return ResponseEntity.status(401).body(Map.of("error", "Incorrect email or password."));
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body(Map.of("error", "An error occurred during login."));
	    }
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) {
	    String authHeader = request.getHeader("Authorization");
	    
	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        return ResponseEntity.badRequest().body("No token provided");
	    }
	    
	    String token = authHeader.substring(7); 
	    tokenBlacklistService.blacklist(token);
	    
	    return ResponseEntity.ok("Logged out successfully");
	}


    @GetMapping("/my-account")
    public Account getMyAccount(HttpServletRequest request) {
        return (Account) request.getAttribute("user"); // comes from JwtFilter
    }
}
