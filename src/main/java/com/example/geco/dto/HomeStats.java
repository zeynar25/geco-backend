package com.example.geco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeStats {
	Long attractionNumber;
	Double averageVisitor;
	Double averageRating;
}
