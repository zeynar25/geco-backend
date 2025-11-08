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
@Table(name="package")
public class TourPackage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private int packageId;
	private int pricePerPerson;
	
	// String list of package inclusions separated by comma.
	private String packageInclusions;
	
	// Short description about the package.
	private String description;
	private int basePrice;
}
