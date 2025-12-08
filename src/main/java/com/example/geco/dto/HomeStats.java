package com.example.geco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeStats {
	Long attractionNumber;
	Long tourPackageNumber;
	Double averageVisitor;
	Double averageRating;
}
