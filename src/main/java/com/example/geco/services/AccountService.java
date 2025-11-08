package com.example.geco.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.geco.domains.Account;
import com.example.geco.domains.UserDetail;
import com.example.geco.dto.SignupRequest;
import com.example.geco.repositories.AccountRepository;
import com.example.geco.repositories.UserDetailRepository;

@Service
public class AccountService {
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private UserDetailRepository userDetailRepository;
	
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	public Account addAccount(SignupRequest request) {
		Account account = request.getAccount();
		UserDetail detail = request.getUserDetail();
		
		if (account.getPassword() == null || account.getPassword().trim().length() < 8) {
		    throw new IllegalArgumentException("Password must have at least 8 characters.");
		}
		
		if (detail.getEmail() == null || detail.getEmail().trim().length() < 5 || !detail.getEmail().contains("@")) {
		    throw new IllegalArgumentException("Please include a proper email address.");
		}

		UserDetail savedDetail = userDetailRepository.save(detail);
		
		// Hashing the plain text password.
		String hashedPassword = passwordEncoder.encode(account.getPassword());
		account.setPassword(hashedPassword);
		account.setDetail(savedDetail);
		
		return accountRepository.save(account);
	}
}