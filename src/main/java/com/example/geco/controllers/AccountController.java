package com.example.geco.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.Account;
import com.example.geco.dto.AccountResponse;
import com.example.geco.dto.DetailRequest;

@RestController
@RequestMapping("/account")
public class AccountController extends AbstractController{
	@PostMapping
	public ResponseEntity<AccountResponse> addAccount(@RequestBody Account account) {
		AccountResponse savedAccount  = accountService.addTouristAccount(account);
		return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
	}
	
	@PostMapping("/admin")
	public ResponseEntity<AccountResponse> addAccountByAdmin(@RequestBody Account account) {
		AccountResponse savedAccount  = accountService.addAccountByAdmin(account);
		return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
	}

	// Can only update account's password.
	@PatchMapping("/update-account/{id}")
	public ResponseEntity<AccountResponse> updateAccount(@PathVariable int id, @RequestBody Account account) {
		account.setAccountId(id);
		AccountResponse savedAccount  = accountService.updatePassword(account);
		return new ResponseEntity<>(savedAccount, HttpStatus.OK);
	}

	// Can update the password and role.
	@PatchMapping("/admin/update-account/{id}")
	public ResponseEntity<AccountResponse> updateAccountByAdmin(@PathVariable int id, @RequestBody Account account) {
		account.setAccountId(id);
		AccountResponse savedAccount  = accountService.updatePassword(account);
		return new ResponseEntity<>(savedAccount, HttpStatus.OK);
	}
	
	@PatchMapping("/update-details/{id}")
	public ResponseEntity<AccountResponse> updateDetail(@PathVariable int id, @RequestBody DetailRequest request) {
		request.setAccountId(id);
		AccountResponse savedAccount  = accountService.updateDetails(request);
		return new ResponseEntity<>(savedAccount, HttpStatus.OK);
	}
}
