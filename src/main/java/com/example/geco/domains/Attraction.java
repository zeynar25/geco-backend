package com.example.geco.domains;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name="attraction")
public class Attraction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer attractionId;
	private String name;
	private String description;
	private String funFact;
	
	private String photo2dUrl;
	
	@Builder.Default
	private boolean isActive = true;
}
