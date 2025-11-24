package com.example.geco.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.Faq;

import jakarta.persistence.LockModeType;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Integer>{
	boolean existsByQuestionIgnoreCase(String question);

	List<Faq> findAllByOrderByDisplayOrder();
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT COALESCE(MAX(f.displayOrder), 0) FROM Faq f")
	int getMaxDisplayOrderForUpdate();
}
