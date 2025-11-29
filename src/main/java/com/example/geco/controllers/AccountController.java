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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/account")
@Tag(name = "Account", description = "Operations about accounts")
public class AccountController extends AbstractController{
	@PostMapping
	@Operation(
			summary = "Create a new tourist account", 
			description = "Adds a new tourist account with the given signup details")
	public ResponseEntity<AccountResponse> addAccount(
			@RequestBody @Valid SignupRequest request) {
		AccountResponse savedAccount  = accountService.addTouristAccount(request);
		return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
	}

	@PatchMapping("/update-password/{id}")
	@Operation(
			summary = "Update account password", 
			description = "Updates the password for an account by ID")
	public ResponseEntity<AccountResponse> updatePassword(
			@PathVariable int id, 
			@RequestBody @Valid PasswordUpdateRequest request) {
		AccountResponse savedAccount  = accountService.updatePassword(id, request);
		return new ResponseEntity<>(savedAccount, HttpStatus.OK);
	}
	
	@PatchMapping("/update-details/{id}")
	@Operation(
			summary = "Update account details", 
			description = "Updates the personal details of an account by ID")
	public ResponseEntity<AccountResponse> updateDetails(
			@PathVariable int id, 
			@RequestBody @Valid DetailRequest request) {
		AccountResponse savedAccount  = accountService.updateDetails(id, request);
		return new ResponseEntity<>(savedAccount, HttpStatus.OK);
	}
	
	
	@GetMapping("staff/list/admin")
	@Operation(
			summary = "Get all admin accounts", 
			description = "Retrieves a list of all admin accounts")
	public ResponseEntity<List<AccountResponse>> getAllAdmins() {
		List<AccountResponse> accounts  = accountService.getAllAdmin();
		return ResponseEntity.ok(accounts);
	}
	
	@GetMapping("staff/list/staff")
	@Operation(
			summary = "Get all staff accounts", 
			description = "Retrieves a list of all staff accounts")
	public ResponseEntity<List<AccountResponse>> getAllStaffs() {
		List<AccountResponse> accounts  = accountService.getAllStaffs();
		return ResponseEntity.ok(accounts);
	}
	
	@GetMapping("staff/list/staff/active")
	@Operation(
			summary = "Get all active staff accounts", 
			description = "Retrieves a list of all active staff accounts")
	public ResponseEntity<List<AccountResponse>> getAllActiveStaffs() {
		List<AccountResponse> accounts  = accountService.getAllActiveStaffs();
		return ResponseEntity.ok(accounts);
	}
	
	@GetMapping("staff/list/staff/inactive")
	@Operation(
			summary = "Get all inactive staff accounts", 
			description = "Retrieves a list of all inactive staff accounts")
	public ResponseEntity<List<AccountResponse>> getAllInactiveStaffs() {
		List<AccountResponse> accounts  = accountService.getAllInactiveStaffs();
		return ResponseEntity.ok(accounts);
	}
	
	@GetMapping("staff/list/user")
    @Operation(
    		summary = "Get all user accounts", 
			description = "Retrieves a list of all regular user accounts")
    public ResponseEntity<List<AccountResponse>> getAllUsers() {
        List<AccountResponse> accounts  = accountService.getAllUsers();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("staff/list/user/active")
    @Operation(
			summary = "Get all active user accounts", 
			description = "Retrieves a list of all active user accounts")
    public ResponseEntity<List<AccountResponse>> getAllActiveUsers() {
        List<AccountResponse> accounts  = accountService.getAllActiveUsers();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("staff/list/user/inactive")
    @Operation(
			summary = "Get all inactive user accounts", 
			description = "Retrieves a list of all inactive user accounts")
    public ResponseEntity<List<AccountResponse>> getAllInactiveUsers() {
        List<AccountResponse> accounts  = accountService.getAllInactiveUsers();
        return ResponseEntity.ok(accounts);
    }

    @PatchMapping("/staff/reset-password/{id}")
    @Operation(
			summary = "Reset password by staff", 
			description = "Resets the password of an account by staff authority")
    public ResponseEntity<AccountResponse> resetPasswordByStaff(
            @PathVariable int id) {
        AccountResponse savedAccount  = accountService.resetPasswordByStaff(id);
        return new ResponseEntity<>(savedAccount, HttpStatus.OK);
    }
	
	
    @PostMapping("/admin")
    @Operation(
			summary = "Create account by admin", 
			description = "Adds a new account created by an admin user")
    public ResponseEntity<AccountResponse> addAccountByAdmin(@RequestBody @Valid SignupRequest request) {
        AccountResponse savedAccount  = accountService.addAccountByAdmin(request);
        return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
    }

    @PatchMapping("/admin/update-role/{id}")
    @Operation(
			summary = "Update account role by admin", 
			description = "Updates the role of an account using admin privileges")
    public ResponseEntity<AccountResponse> updateAccountRoleByAdmin(
            @PathVariable int id, 
            @RequestBody @Valid RoleUpdateRequest request) {
        AccountResponse savedAccount  = accountService.updateRoleByAdmin(id, request);
        return new ResponseEntity<>(savedAccount, HttpStatus.OK);
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
			summary = "Soft delete account", 
			description = "Soft deletes an account by ID (admin only)")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable int id) {
        accountService.softDeleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/admin/restore/{id}")
    @Operation(
			summary = "Restore soft-deleted account", 
			description = "Restores a previously soft-deleted account by ID")
    public ResponseEntity<Void> restoreAccount(@PathVariable int id) {
        accountService.restoreAccount(id);
        return ResponseEntity.noContent().build();
    }
}
