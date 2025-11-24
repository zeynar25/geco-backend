package com.example.geco.domains;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
		name="package_inclusion",
		uniqueConstraints = @UniqueConstraint(columnNames = "inclusionName")
)
public class PackageInclusion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Integer inclusionId;
	private String inclusionName;
	private Integer inclusionPricePerPerson;
	
	@Builder.Default
	private boolean isActive = true;
}
