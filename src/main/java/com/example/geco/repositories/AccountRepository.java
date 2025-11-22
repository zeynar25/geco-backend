package com.example.geco.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.Account;
import com.example.geco.domains.Account.Role;


@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>{
	Account findByDetailEmail(String email);

	List<Account> findAllByRoleOrderByDetail_Email(Role role);
}