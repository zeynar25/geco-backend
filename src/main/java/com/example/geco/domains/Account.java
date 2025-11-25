package com.example.geco.domains;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="user_account")
public class Account implements UserDetails {
	public enum Role {
	    ADMIN,
	    STAFF,
	    USER
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Integer accountId;
	
	@Enumerated(EnumType.STRING)
    private Role role;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "detailId", referencedColumnName = "detailId")
    private UserDetail detail;
	
	private String password;
	
	@Builder.Default
	private boolean isActive = true;
	
	@JsonIgnore
	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
		if (role == null) return List.of();
		
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
	
	@Override
	public String getUsername() {
		return detail.getEmail();
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override 
	public boolean isAccountNonExpired() { 
		return true; 
	}

    @Override 
    public boolean isAccountNonLocked() { 
    	return true; 
	}

	@JsonIgnore
    @Override 
    public boolean isCredentialsNonExpired() { 
    	return true; 
	}

	@JsonIgnore
    @Override 
    public boolean isEnabled() { 
    	return isActive; 
	}
}
