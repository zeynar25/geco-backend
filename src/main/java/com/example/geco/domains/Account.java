package com.example.geco.domains;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user_account")
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Integer accountId;

	@OneToOne
	@JoinColumn(name = "detailId", referencedColumnName = "detailId")
    private UserDetail detail;
	
	private String password;
}
