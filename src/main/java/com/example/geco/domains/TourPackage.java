package com.example.geco.domains;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name="tour_package")
public class TourPackage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Integer packageId;
	
	private String name;
	private Integer duration; // in minutes.
	
	private int minPerson;
	private int maxPerson;
	
	private String description;
	private Integer basePrice;
	
	private String notes;
	
	@ManyToMany
    @JoinTable(
        name = "package_inclusion_map",
        joinColumns = @JoinColumn(name = "package_id"),
        inverseJoinColumns = @JoinColumn(name = "inclusion_id")
    )
	@Builder.Default
    private List<PackageInclusion> inclusions = new ArrayList<>();
	
	@Builder.Default
	private boolean isActive = true;
}
