package com.example.geco.controllers;

import org.springframework.http.ResponseEntity;
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
		String token = authService.verify(request);
        return ResponseEntity.ok(token);
	}

    @GetMapping("/my-account")
    public Account getMyAccount(HttpServletRequest request) {
        return (Account) request.getAttribute("user"); // comes from JwtFilter
    }
}
