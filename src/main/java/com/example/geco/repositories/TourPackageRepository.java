package com.example.geco.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.TourPackage;

@Repository
public interface TourPackageRepository extends JpaRepository<TourPackage, Integer>{

	List<TourPackage> findAllByOrderByName();

}
