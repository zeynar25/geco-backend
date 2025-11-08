package com.example.geco.domains;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user_detail")
public class UserDetail {
	@Id
	int userId;
	
	@OneToOne
	@JoinColumn(name = "userId", referencedColumnName = "userId")
    private Account account;

	private String email;
	private String name;
	private String contactNumber;
	
}
