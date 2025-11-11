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
@Table(name="discount")
public class Discount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Integer discountId;
	private Integer groupSize;
	private Double percentOff;
}
