package com.example.geco.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.dto.AccountResponse;
import com.example.geco.dto.DetailRequest;
import com.example.geco.dto.PasswordUpdateRequest;
import com.example.geco.dto.RoleUpdateRequest;
import com.example.geco.dto.SignupRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController extends AbstractController{
	@PostMapping
	public ResponseEntity<AccountResponse> addAccount(@RequestBody @Valid SignupRequest request) {
		AccountResponse savedAccount  = accountService.addTouristAccount(request);
		return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
	}

	@PatchMapping("/update-password/{id}")
	public ResponseEntity<AccountResponse> updatePassword(
			@PathVariable int id, 
			@RequestBody @Valid PasswordUpdateRequest request) {
		AccountResponse savedAccount  = accountService.updatePassword(id, request);
		return new ResponseEntity<>(savedAccount, HttpStatus.OK);
	}
	
	@PatchMapping("/update-details/{id}")
	public ResponseEntity<AccountResponse> updateDetails(
			@PathVariable int id, 
			@RequestBody DetailRequest request) {
		AccountResponse savedAccount  = accountService.updateDetails(id, request);
		return new ResponseEntity<>(savedAccount, HttpStatus.OK);
	}
	
	
	@GetMapping("staff/list/admin")
	public ResponseEntity<List<AccountResponse>> getAllAdmins() {
		List<AccountResponse> accounts  = accountService.getAllAdmin();
		return ResponseEntity.ok(accounts);
	}
	
	@GetMapping("staff/list/staff")
	public ResponseEntity<List<AccountResponse>> getAllStaffs() {
		List<AccountResponse> accounts  = accountService.getAllStaffs();
		return ResponseEntity.ok(accounts);
	}
	
	@GetMapping("staff/list/staff/active")
	public ResponseEntity<List<AccountResponse>> getAllActiveStaffs() {
		List<AccountResponse> accounts  = accountService.getAllActiveStaffs();
		return ResponseEntity.ok(accounts);
	}
	
	@GetMapping("staff/list/staff/inactive")
	public ResponseEntity<List<AccountResponse>> getAllInactiveStaffs() {
		List<AccountResponse> accounts  = accountService.getAllInactiveStaffs();
		return ResponseEntity.ok(accounts);
	}
	
	@GetMapping("staff/list/guest")
	public ResponseEntity<List<AccountResponse>> getAllGuests() {
		List<AccountResponse> accounts  = accountService.getAllGuests();
		return ResponseEntity.ok(accounts);
	}
	
	@GetMapping("staff/list/guest/active")
	public ResponseEntity<List<AccountResponse>> getAllActiveGuests() {
		List<AccountResponse> accounts  = accountService.getAllActiveGuests();
		return ResponseEntity.ok(accounts);
	}
	
	@GetMapping("staff/list/guest/inactive")
	public ResponseEntity<List<AccountResponse>> getAllInactiveGuests() {
		List<AccountResponse> accounts  = accountService.getAllInactiveGuests();
		return ResponseEntity.ok(accounts);
	}
	
	@PatchMapping("/staff/reset-password/{id}")
	public ResponseEntity<AccountResponse> resetPasswordByStaff(
			@PathVariable int id) {
		AccountResponse savedAccount  = accountService.resetPasswordByStaff(id);
		return new ResponseEntity<>(savedAccount, HttpStatus.OK);
	}
	
	
	@PostMapping("/admin")
	public ResponseEntity<AccountResponse> addAccountByAdmin(@RequestBody @Valid SignupRequest request) {
		AccountResponse savedAccount  = accountService.addAccountByAdmin(request);
		return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
	}
	
	@PatchMapping("/admin/update-role/{id}")
	public ResponseEntity<AccountResponse> updateAccountRoleByAdmin(
			@PathVariable int id, 
			@RequestBody RoleUpdateRequest request) {
		AccountResponse savedAccount  = accountService.updateRoleByAdmin(id, request);
		
		return new ResponseEntity<>(savedAccount, HttpStatus.OK);
	}
	
	@DeleteMapping("/admin/{id}")
	public ResponseEntity<Void> deleteAccount(
			@PathVariable int id) {
		accountService.softDeleteAccount(id);
		return ResponseEntity.noContent().build();
	}
	
	@PatchMapping("/admin/restore/{id}")
	public ResponseEntity<Void> restoreAccount(@PathVariable int id) {
	    accountService.restoreAccount(id);
	    return ResponseEntity.noContent().build();
	}
}
