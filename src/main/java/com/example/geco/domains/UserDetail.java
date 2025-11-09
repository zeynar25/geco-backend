package com.example.geco.domains;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	Integer detailId;

	private String email;
	private String surname;
	private String firstName;
	private String contactNumber;
	
}
