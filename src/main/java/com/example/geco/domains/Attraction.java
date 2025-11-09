package com.example.geco.domains;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="atttraction")
public class Attraction {
	@Id
	private Integer attractionId;
	private String name;
	private String description;
}
