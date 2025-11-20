package com.example.geco.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.geco.dto.LoginRequest;

@Service
public class AuthService {

	@Autowired
	private AuthenticationManager authManager;

	@Autowired
	private JwtService jwtService;

	public String verify(LoginRequest request) {
		Authentication authentication = 
				authManager.authenticate(new UsernamePasswordAuthenticationToken(
						request.getEmail(), 
						request.getPassword()));
		
		if (!authentication.isAuthenticated()) {
			throw new IllegalArgumentException("Invalid credentials.");
		} 

		return jwtService.generateToken(request.getEmail());
	}
}
