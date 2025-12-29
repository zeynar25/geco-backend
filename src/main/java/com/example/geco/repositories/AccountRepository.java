package com.example.geco.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.Account;
import com.example.geco.domains.Account.Role;


@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>{
	Optional<Account> findByDetailEmail(String email);
	
	boolean existsByDetailEmail(String email);
	
	Page<Account> findAllByOrderByDetail_Email(Pageable pageable);

	Page<Account> findAllByIsActiveOrderByDetail_Email(boolean isActive, Pageable pageable);

	Page<Account> findAllByRoleOrderByDetail_Email(Role role, Pageable pageable);

	Page<Account> findAllByRoleAndIsActiveOrderByDetail_Email(Role staff, boolean isActive, Pageable pageable);

	Page<Account> findAllByDetail_EmailContainingIgnoreCaseOrderByDetail_Email(
		    String email,
		    Pageable pageable
		);

	Page<Account> findAllByDetail_EmailContainingIgnoreCaseAndRoleOrderByDetail_Email(
	    String email,
	    Role role,
	    Pageable pageable
	);

	Page<Account> findAllByDetail_EmailContainingIgnoreCaseAndIsActiveOrderByDetail_Email(
	    String email,
	    boolean isActive,
	    Pageable pageable
	);

	Page<Account> findAllByDetail_EmailContainingIgnoreCaseAndRoleAndIsActiveOrderByDetail_Email(
	    String email,
	    Role role,
	    boolean isActive,
	    Pageable pageable
	);
}