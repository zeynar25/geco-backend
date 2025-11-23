package com.example.geco.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.geco.domains.Account;
import com.example.geco.domains.UserDetail;
import com.example.geco.repositories.AccountRepository;

@Configuration
public class AdminSetup {

    @Bean
    public CommandLineRunner createAdmin(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if admin already exists
            if (accountRepository.findByDetailEmail("admin@example.com").isEmpty()) {

                UserDetail adminDetail = UserDetail.builder()
                        .email("admin@example.com")
                        .firstName("Admin")
                        .surname("User")
                        .build();

                Account admin = Account.builder()
                        .detail(adminDetail)
                        .role(Account.Role.ADMIN)
                        .password(passwordEncoder.encode("admin123")) // hashed
                        .isActive(true)
                        .build();

                accountRepository.save(admin);
                // System.out.println("Admin account created: admin@example.com / admin123");
            }
        };
    }
}
