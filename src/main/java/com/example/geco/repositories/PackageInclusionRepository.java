package com.example.geco.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.PackageInclusion;

@Repository
public interface PackageInclusionRepository extends JpaRepository<PackageInclusion, Integer>{

	List<PackageInclusion> findAllByOrderByInclusionName();

}
