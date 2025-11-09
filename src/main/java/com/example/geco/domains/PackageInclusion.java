package com.example.geco.domains;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="package_inclusion")
public class PackageInclusion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Integer inclusionId;
	private String inclusionName;
	private Integer inclusionPrice;
}
