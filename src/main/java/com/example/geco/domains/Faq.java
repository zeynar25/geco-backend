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
@Table(name="faq")
public class Faq {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Integer faqId;
	private String question;
	private String answer;
	
	private Integer displayOrder;
	
	@Builder.Default
	private boolean isActive = true;
}