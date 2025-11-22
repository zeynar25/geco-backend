package com.example.geco.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.Faq;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Integer>{
	boolean existsByQuestionIgnoreCase(String question);

	List<Faq> findAllByOrderByDisplayOrder();
}
