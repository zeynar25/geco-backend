package com.example.geco.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.geco.filters.JwtFilter;
import com.example.geco.services.AccountService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private JwtFilter jwtFilter;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		return http.csrf(customizer -> 
				customizer.disable())
			.authorizeHttpRequests(request -> request
					.requestMatchers("/account/admin/**").hasRole("ADMIN")
					.requestMatchers("/account/staff/**").hasAnyRole("STAFF", "ADMIN")
					
					.requestMatchers(HttpMethod.GET, "/attraction/**").permitAll()
					.requestMatchers(HttpMethod.DELETE, "/attraction/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.POST, "/attraction/**").hasAnyRole("STAFF", "ADMIN")
					.requestMatchers(HttpMethod.PATCH, "/attraction/**").hasAnyRole("STAFF", "ADMIN")
					
					.requestMatchers(HttpMethod.GET, "/faq/active").permitAll()
					.requestMatchers(HttpMethod.GET, "/faq/**").hasAnyRole("STAFF","ADMIN")
					.requestMatchers(HttpMethod.DELETE, "/faq/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.POST, "/faq/**").hasAnyRole("STAFF", "ADMIN")
					.requestMatchers(HttpMethod.PATCH, "/faq/**").hasAnyRole("STAFF", "ADMIN")
					
					.requestMatchers(HttpMethod.GET, "/package-inclusion/active").permitAll()
					.requestMatchers("/package-inclusion/staff/**").hasAnyRole("STAFF", "ADMIN")
					.requestMatchers("/package-inclusion/admin/**").hasRole("ADMIN")
					
					.requestMatchers(HttpMethod.GET, "/feedback-category/active").hasRole("USER")
		            .requestMatchers(HttpMethod.GET, "/feedback-category/**").hasAnyRole("STAFF", "ADMIN") 
		            .requestMatchers(HttpMethod.POST, "/feedback-category/**").hasAnyRole("STAFF", "ADMIN")
		            .requestMatchers(HttpMethod.PATCH, "/feedback-category/{id}").hasAnyRole("STAFF", "ADMIN")
		            .requestMatchers(HttpMethod.PATCH, "/feedback-category/admin/restore/**").hasRole("ADMIN")
		            .requestMatchers(HttpMethod.DELETE, "/feedback-category/admin/**").hasRole("ADMIN")
		            
		            .requestMatchers(HttpMethod.GET, "/package/active").permitAll()
		            .requestMatchers(HttpMethod.GET, "/package/*/inclusions/available").permitAll()
		            .requestMatchers(HttpMethod.POST, "/package").hasAnyRole("STAFF", "ADMIN")
		            .requestMatchers(HttpMethod.PATCH, "/package/{id}").hasAnyRole("STAFF", "ADMIN")
		            .requestMatchers(HttpMethod.DELETE, "/package/{id}").hasRole("ADMIN")
		            .requestMatchers(HttpMethod.PATCH, "/package/admin/restore/{id}").hasRole("ADMIN")
		            .requestMatchers(HttpMethod.GET, "/package").hasAnyRole("STAFF", "ADMIN")
		            .requestMatchers(HttpMethod.GET, "/package/inactive").hasAnyRole("STAFF", "ADMIN")

					.requestMatchers(HttpMethod.DELETE, "/booking/**").hasRole("ADMIN")
					.requestMatchers("/account/my-account").authenticated()
					
		            .anyRequest().permitAll())
			.httpBasic(Customizer.withDefaults())
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder(12);
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider(
			AccountService accountService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(accountService);
		provider.setPasswordEncoder(passwordEncoder);
		
		return provider;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}

}
