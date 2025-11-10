package com.example.geco.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.dto.AccountResponse;
import com.example.geco.dto.SignupRequest;

@RestController
@RequestMapping("/account")
public class AccountController extends AbstractController{
	@PostMapping
	public ResponseEntity<?> addAccount(@RequestBody SignupRequest request) {
		AccountResponse savedAccount  = accountService.addAccount(request);
		return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
	}
	
	@PatchMapping
	public ResponseEntity<?> updateAccount(@RequestBody SignupRequest request) {
		AccountResponse savedAccount  = accountService.updateAccount(request);
		return new ResponseEntity<>(savedAccount, HttpStatus.OK);
	}
}
