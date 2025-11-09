package com.example.geco.domains;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="feedback_category")
public class FeedbackCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Integer feedbackCategoryId;
	
	private String label;
}
