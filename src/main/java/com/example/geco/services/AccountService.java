package com.example.geco.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.geco.domains.Account;
import com.example.geco.domains.Account.Role;
import com.example.geco.domains.AuditLog.LogAction;
import com.example.geco.domains.UserDetail;
import com.example.geco.dto.AccountResponse;
import com.example.geco.dto.AccountResponse.PasswordStatus;
import com.example.geco.dto.DetailRequest;
import com.example.geco.dto.PasswordUpdateRequest;
import com.example.geco.dto.RoleUpdateRequest;
import com.example.geco.dto.SignupRequest;
import com.example.geco.repositories.AccountRepository;

import jakarta.persistence.EntityNotFoundException; 

@Service
@Transactional
public class AccountService extends BaseService implements UserDetailsService{
	@Autowired
	private AccountRepository accountRepository;
	
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = accountRepository.findByDetailEmail(username)
		        .orElseThrow(() -> new UsernameNotFoundException("Account with Email \"" + username + "\" not found."));
		
        return account;
	}
	
	private AccountResponse toResponse(Account account, AccountResponse.PasswordStatus status) {
	    UserDetail detail = account.getDetail();

	    return AccountResponse.builder()
	            .accountId(account.getAccountId())
	            .role(account.getRole())
	            .passwordStatus(status)
	            .detailId(detail != null ? detail.getDetailId() : null)
	            .surname(detail != null ? detail.getSurname() : null)
	            .firstName(detail != null ? detail.getFirstName() : null)
	            .email(detail != null ? detail.getEmail() : null)
	            .contactNumber(detail != null ? detail.getContactNumber() : null)
	            .isActive(account.isActive())
	            .build();
	}
	
	public Account createAccountCopy(Account account) {
		return Account.builder()
				.accountId(account.getAccountId())
				.role(account.getRole())
				.detail(account.getDetail())
				.password(account.getPassword())
				.isActive(account.isActive())
			    .build();
	}
	
	public UserDetail createUserDetailCopy(UserDetail detail) {
		return UserDetail.builder()
			    .detailId(detail.getDetailId())
			    .email(detail.getEmail())
			    .firstName(detail.getFirstName())
			    .surname(detail.getSurname())
			    .contactNumber(detail.getContactNumber())
			    .build();
	}
	
	
	
	
	
	
	
	public AccountResponse addAccount(SignupRequest request) {
		String email = request.getEmail().trim();
		String password = request.getPassword().trim();
		String confirmPassword = request.getConfirmPassword().trim();
		Role role = request.getRole();
		
		if (accountRepository.existsByDetailEmail(email)) {
		    throw new IllegalArgumentException("Email '" + email + "' is already registered.");
		}
		
		if (role == null) {
			throw new IllegalArgumentException("Role must be specified.");
		}
		
		if (!password.equals(confirmPassword)) {
			throw new IllegalArgumentException("Password and Confirm password must match.");
		}
		
		// Hashing the plain text password.
		String hashedPassword = passwordEncoder.encode(password);
		Account account = Account.builder()
				.role(role)
				.password(hashedPassword)
				.detail(
						UserDetail.builder()
						.email(email)
						.build())
				.build();
		
		Account savedAccount = accountRepository.save(account);
		
		logIfAuthenticatedStaffOrAdmin("Account", (long)savedAccount.getAccountId(), LogAction.CREATE, null, savedAccount);
		
		return toResponse(savedAccount, AccountResponse.PasswordStatus.UNCHANGED);
	}

	public AccountResponse addTouristAccount(SignupRequest request) {
		SignupRequest userRequest = new SignupRequest(
	            request.getEmail(),
	            request.getPassword(),
	            request.getConfirmPassword(),
	            Role.USER
	    );
		
		return addAccount(userRequest);
	}
	
	public AccountResponse addAccountByStaff(SignupRequest request) {
		if (request.getRole() == null) {
		    throw new IllegalArgumentException("Role must be specified when staff creates an account.");
		}
		
		if (request.getRole().equals(Role.ADMIN)) {
			throw new IllegalArgumentException("A staff cannot add an admin account.");
		}
		
		return addAccount(request);
	}
	
	public AccountResponse addAccountByAdmin(SignupRequest request) {
		if (request.getRole() == null) {
		    throw new IllegalArgumentException("Role must be specified when admin creates an account.");
		}
		
		return addAccount(request);
	}
	
	@Transactional(readOnly = true)
	public Account getAccount(int id) {
		Account account = accountRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Account with ID '" + id + "' not found."));
        return account;
	}
	
	@Transactional(readOnly = true)
	public Account getAccount(String email) {
		Account account = accountRepository.findByDetailEmail(email)
	            .orElseThrow(() -> new EntityNotFoundException("Account with email '" + email + "' not found."));
        return account;
	}
	
	@Transactional(readOnly = true)
	public Page<AccountResponse> getAllAccounts(Pageable pageable) {
	    return accountRepository.findAllByOrderByDetail_Email(pageable)
	            .map(a -> toResponse(a, PasswordStatus.UNCHANGED));
	}

	@Transactional(readOnly = true)
	public Page<AccountResponse> getAllActiveAccounts(Pageable pageable) {
	    return accountRepository.findAllByIsActiveOrderByDetail_Email(true, pageable)
	            .map(a -> toResponse(a, PasswordStatus.UNCHANGED));
	}

	@Transactional(readOnly = true)
	public Page<AccountResponse> getAllInactiveAccounts(Pageable pageable) {
	    return accountRepository.findAllByIsActiveOrderByDetail_Email(false, pageable)
	            .map(a -> toResponse(a, PasswordStatus.UNCHANGED));
	}


	@Transactional(readOnly = true)
	public Page<AccountResponse> getAllAdmins(Pageable pageable) {
		return accountRepository.findAllByRoleOrderByDetail_Email(
				Account.Role.ADMIN, pageable)
				.map(a -> toResponse(a, PasswordStatus.UNCHANGED));
	}


	@Transactional(readOnly = true)
	public Page<AccountResponse> getAllStaffs(Pageable pageable) {
		return accountRepository.findAllByRoleOrderByDetail_Email(
				Account.Role.STAFF, pageable)
				.map(a -> toResponse(a, PasswordStatus.UNCHANGED));
	}

	@Transactional(readOnly = true)
	public Page<AccountResponse> getAllActiveStaffs(Pageable pageable) {
		return accountRepository.findAllByRoleAndIsActiveOrderByDetail_Email(
				Account.Role.STAFF, true, pageable)
				.map(a -> toResponse(a, PasswordStatus.UNCHANGED));
	}

	@Transactional(readOnly = true)
	public Page<AccountResponse> getAllInactiveStaffs(Pageable pageable) {
		return accountRepository.findAllByRoleAndIsActiveOrderByDetail_Email(
				Account.Role.STAFF, false, pageable)
				.map(a -> toResponse(a, PasswordStatus.UNCHANGED));
	}

	@Transactional(readOnly = true)
	public Page<AccountResponse> getAllUsers(Pageable pageable) {
		return accountRepository.findAllByRoleOrderByDetail_Email(
				Account.Role.USER, pageable)
				.map(a -> toResponse(a, PasswordStatus.UNCHANGED));
	}

	@Transactional(readOnly = true)
	public Page<AccountResponse> getAllActiveUsers(Pageable pageable) {
		return accountRepository.findAllByRoleAndIsActiveOrderByDetail_Email(
				Account.Role.USER, true, pageable)
				.map(a -> toResponse(a, PasswordStatus.UNCHANGED));
	}

	@Transactional(readOnly = true)
	public Page<AccountResponse> getAllInactiveUsers(Pageable pageable) {
		return accountRepository.findAllByRoleAndIsActiveOrderByDetail_Email(
				Account.Role.USER, false, pageable)
				.map(a -> toResponse(a, PasswordStatus.UNCHANGED));
	}
	
	@Transactional(readOnly = true)
	public Page<AccountResponse> searchAccountsByEmail(String emailQuery, Pageable pageable) {
	    String q = emailQuery == null ? "" : emailQuery.trim();

	    if (q.isEmpty()) {
	        // you can either return empty, or all accounts; choose what you prefer.
	        return accountRepository.findAll(pageable)
	                .map(a -> toResponse(a, PasswordStatus.UNCHANGED));
	    }

	    return accountRepository
	            .findAllByDetail_EmailContainingIgnoreCaseOrderByDetail_Email(q, pageable)
	            .map(a -> toResponse(a, PasswordStatus.UNCHANGED));
	}
	
	
	public AccountResponse updatePassword(int id, PasswordUpdateRequest request) {
		Account existingAccount = accountRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Account with ID '" + id + "' not found."));

		checkAuth(id);

	    String oldPasswordEntered = request.getOldPassword().trim();
		if(!passwordEncoder.matches(oldPasswordEntered, existingAccount.getPassword())) {
            throw new IllegalArgumentException("Wrong old password input.");
		}
	    
	    String newPassword = request.getPassword().trim();
	    String newConfirmPassword = request.getConfirmPassword().trim();
		if(passwordEncoder.matches(newPassword, existingAccount.getPassword())) {
            throw new IllegalArgumentException("This is already your password.");
		}
		
		if (!newPassword.equals(newConfirmPassword)) {
			throw new IllegalArgumentException("Password and Confirm password must match.");
		}

		String hashedPassword = passwordEncoder.encode(newPassword);
        existingAccount.setPassword(hashedPassword);
        
        accountRepository.save(existingAccount);
        
        return toResponse(existingAccount, PasswordStatus.CHANGED_SUCCESSFULLY);
	}
	
	public AccountResponse updateDetails(int id, DetailRequest request) {
		Account existingAccount = accountRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Account with ID '" + id + "' not found."));
		
		checkAuth(id);

		UserDetail existingDetail = existingAccount.getDetail();
		
		UserDetail prevDetail = createUserDetailCopy(existingDetail);
		
		String email = request.getEmail() != null ? request.getEmail().trim() : "";
		String surname = request.getSurname() != null ? request.getSurname().trim() : "";
		String firstName = request.getFirstName() != null ? request.getFirstName().trim() : "";
		String contactNumber = request.getContactNumber() != null ? request.getContactNumber().trim() : "";
		
        // Update email if provided.
        if (!email.isBlank()) {
            if (!email.contains("@") || email.length() < 5) {
                throw new IllegalArgumentException("Please include a proper email address.");
            }
            
            if (accountRepository.existsByDetailEmail(email) &&
        	    !existingDetail.getEmail().equals(email)) {
        	    throw new IllegalArgumentException("Email '" + email + "' is already registered.");
        	}
            
            existingDetail.setEmail(email);
        }

        // Update surname if provided.
        if (!surname.isBlank()) {
        	existingDetail.setSurname(surname);
        }

        // Update first name if provided.
        if (!firstName.isBlank()) {
        	existingDetail.setFirstName(firstName);
        }

        // Update contact number if provided.
        if (!contactNumber.isBlank()) {
        	existingDetail.setContactNumber(contactNumber);
        }

		accountRepository.save(existingAccount);

		logIfStaffOrAdmin("UserDetail", (long) id, LogAction.UPDATE, prevDetail, existingDetail);

		return toResponse(existingAccount, PasswordStatus.UNCHANGED);
	}

	
	public AccountResponse updateRoleByAdmin(int id, RoleUpdateRequest request) {
		if (request.getRole() == null) {
			throw new IllegalArgumentException("Can't update role without role.");
		}
		
		Account existingAccount = accountRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Account with ID '" + id + "' not found."));
		
		
		if (existingAccount.getRole().equals(request.getRole())) {
		    throw new IllegalArgumentException("Account already has role " + request.getRole());
		}
		
		Account prevAccount = createAccountCopy(existingAccount);
		
		existingAccount.setRole(request.getRole());

		Account saved = accountRepository.save(existingAccount);
		
		logIfStaffOrAdmin("Account", (long) id, LogAction.UPDATE, prevAccount, existingAccount);

	    return toResponse(saved, PasswordStatus.UNCHANGED);
	}

	public AccountResponse resetPasswordByStaff(int id) {
		Account existingAccount = accountRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Account with ID '" + id + "' not found."));
		
		String newPassword = "";
		
		if (passwordEncoder.matches(newPassword, existingAccount.getPassword())) {
		    throw new IllegalArgumentException("New password must be different from current password.");
		}
		
		Account prevAccount = createAccountCopy(existingAccount);
        
		String hashedPassword = passwordEncoder.encode(newPassword);
        existingAccount.setPassword(hashedPassword);

		Account saved = accountRepository.save(existingAccount);

		logIfStaffOrAdmin("Account", (long) id, LogAction.UPDATE, prevAccount, existingAccount);
		
	    return toResponse(saved, PasswordStatus.RESET_SUCCESSFULLY);
	}

	public void softDeleteAccount(int id) {
	    Account existingAccount = accountRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Account with ID '" + id + "' not found."));
	
	    if (existingAccount.getRole() == Role.ADMIN) {
	        throw new IllegalArgumentException("Cannot delete an admin account.");
	    }

	    if (!existingAccount.isActive()) {
	        throw new IllegalStateException("Account is already inactive.");
	    }
	    
	    Account prevAccount = createAccountCopy(existingAccount);
	
	    // Soft delete: mark account as inactive
	    existingAccount.setActive(false);
	    accountRepository.save(existingAccount);
	
	    logIfStaffOrAdmin("Account", (long) id, LogAction.DISABLE, prevAccount, existingAccount);
	}

	public void restoreAccount(int id) {
		Account account = accountRepository.findById(id)
		        .orElseThrow(() -> new EntityNotFoundException("Account ID '" + id + "' not found."));

	    if (account.isActive()) {
	        throw new IllegalStateException("Account is already active.");
	    }
	    
	    Account prevAccount = createAccountCopy(account);

	    account.setActive(true);
	    accountRepository.save(account);
	    
	    logIfStaffOrAdmin("Account", (long) id, LogAction.RESTORE, prevAccount, account);
	}

}