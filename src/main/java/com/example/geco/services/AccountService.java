package com.example.geco.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.geco.domains.Account;
import com.example.geco.domains.UserDetail;
import com.example.geco.dto.AccountResponse;
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
	
	public AccountResponse addAccount(SignupRequest request) {
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
		String censoredPassword = "*".repeat(account.getPassword().length());
		String hashedPassword = passwordEncoder.encode(account.getPassword());
		account.setPassword(hashedPassword);
		account.setDetail(savedDetail);
		accountRepository.save(account);
		
		return new AccountResponse(
				censoredPassword,
				savedDetail.getSurname(), 
				savedDetail.getFirstName(), 
				savedDetail.getEmail(), 
				savedDetail.getContactNumber()
		);
	}
	
	public AccountResponse updateAccount(SignupRequest request) {
		Account account = request.getAccount();
		UserDetail detail = request.getUserDetail();
		
		Account existingAccount = accountRepository.findById(account.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("Account not found."));

		// Update password if provided.
        String censoredPassword = "No changes";
        if (account.getPassword() != null && !account.getPassword().isBlank()) {
            if (account.getPassword().trim().length() < 8) {
                throw new IllegalArgumentException("Password must have at least 8 characters.");
            }
            
            censoredPassword = "*".repeat(account.getPassword().length());
    		String hashedPassword = passwordEncoder.encode(account.getPassword());
            existingAccount.setPassword(hashedPassword);
        }
        
        UserDetail existingDetail = existingAccount.getDetail();
		
        // Update email if provided.
        if (detail.getEmail() != null && !detail.getEmail().isBlank()) {
            if (!detail.getEmail().contains("@") || detail.getEmail().trim().length() < 5) {
                throw new IllegalArgumentException("Please include a proper email address.");
            }
            existingDetail.setEmail(detail.getEmail());
        }

        // Update surname if provided.
        if (detail.getSurname() != null) {
        	existingDetail.setSurname(detail.getSurname());
        }

        // Update first name if provided.
        if (detail.getFirstName() != null) {
        	existingDetail.setFirstName(detail.getFirstName());
        }

        // Update contact number if provided.
        if (detail.getContactNumber() != null) {
        	existingDetail.setContactNumber(detail.getContactNumber());
        }

		userDetailRepository.save(existingDetail);
		accountRepository.save(existingAccount);
		
		return new AccountResponse(
				censoredPassword,
				existingDetail.getSurname(), 
				existingDetail.getFirstName(), 
				existingDetail.getEmail(), 
				existingDetail.getContactNumber()
		);
	}
}