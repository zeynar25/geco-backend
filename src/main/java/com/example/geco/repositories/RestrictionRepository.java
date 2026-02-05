package com.example.geco.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.Restriction;

@Repository
public interface RestrictionRepository extends JpaRepository<Restriction, Integer>{
	Optional<Restriction> findByNameIgnoreCase(String name);
}
