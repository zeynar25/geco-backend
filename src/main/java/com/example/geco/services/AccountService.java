package com.example.geco.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.geco.domains.Account;
import com.example.geco.domains.Attraction;
import com.example.geco.domains.UserDetail;
import com.example.geco.dto.AccountResponse;
import com.example.geco.dto.DetailRequest;
import com.example.geco.repositories.AccountRepository;
import com.example.geco.repositories.UserDetailRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AccountService {
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private UserDetailRepository userDetailRepository;
	
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	private void validateResponse(Account account, UserDetail detail) {
		if (account == null) {
			throw new IllegalArgumentException("Account is null.");
		}
		
		if (detail == null) {
			throw new IllegalArgumentException("Account detail is null.");
		}
	}
	
	private AccountResponse toResponse(Account account, String censoredPassword) {
		UserDetail detail = account.getDetail();
        return new AccountResponse(
                account.getAccountId(),
                censoredPassword,
                detail.getDetailId(),
                detail.getSurname(),
                detail.getFirstName(),
                detail.getEmail(),
                detail.getContactNumber()
        );
	}
	
	public AccountResponse addAccount(Account account) {
		UserDetail detail = account.getDetail();
		validateResponse(account, detail);
		
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
		
		return toResponse(account, censoredPassword);
	}
	
	public AccountResponse updatePassword(Account account) {
		if (account.getAccountId() == null) {
			throw new IllegalArgumentException("Account ID is null.");
		}
		
		Account existingAccount = accountRepository.findById(account.getAccountId())
				.orElseThrow(() -> new EntityNotFoundException("Account not found."));

        if (account.getPassword() == null || account.getPassword().trim().length() < 8) {
            throw new IllegalArgumentException("Password must have at least 8 characters.");
        }
		
		if(passwordEncoder.matches(account.getPassword(), existingAccount.getPassword())) {
            throw new IllegalArgumentException("This is already your password.");
		}

		String hashedPassword = passwordEncoder.encode(account.getPassword());
        existingAccount.setPassword(hashedPassword);
        
        accountRepository.save(existingAccount);
        
        return toResponse(
        		existingAccount, "*".repeat(account.getPassword().length())
        );
	}
	
	public AccountResponse updateDetails(DetailRequest request) {
		if (request.getAccountId() == null) {
			throw new IllegalArgumentException("Account ID is null.");
		}
			
		Account existingAccount = accountRepository.findById(request.getAccountId())
				.orElseThrow(() -> new EntityNotFoundException("Account not found."));
		
		UserDetail existingDetail = existingAccount.getDetail();
		
        // Update email if provided.
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!request.getEmail().contains("@") || request.getEmail().trim().length() < 5) {
                throw new IllegalArgumentException("Please include a proper email address.");
            }
            existingDetail.setEmail(request.getEmail());
        }

        // Update surname if provided.
        if (request.getSurname() != null) {
        	existingDetail.setSurname(request.getSurname());
        }

        // Update first name if provided.
        if (request.getFirstName() != null) {
        	existingDetail.setFirstName(request.getFirstName());
        }

        // Update contact number if provided.
        if (request.getContactNumber() != null) {
        	existingDetail.setContactNumber(request.getContactNumber());
        }

		accountRepository.save(existingAccount);

		return toResponse(existingAccount, "No changes made");
	}
	
	public AccountResponse updateAccount(Account account) {
		UserDetail detail = account.getDetail();
		validateResponse(account, detail);
		
		Account existingAccount = accountRepository.findById(account.getAccountId())
				.orElseThrow(() -> new EntityNotFoundException("Account not found."));

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

		accountRepository.save(existingAccount);
		

		return toResponse(existingAccount, censoredPassword);
	}
	
	public Account getAccount(int id) {
		Account account = accountRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Account with ID \"" + id + "\" not found."));
        return account;
	}
}