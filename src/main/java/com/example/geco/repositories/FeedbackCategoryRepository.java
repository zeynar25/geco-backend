package com.example.geco.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.geco.domains.FeedbackCategory;

@Repository
public interface FeedbackCategoryRepository extends JpaRepository<FeedbackCategory, Integer> {
	boolean existsByLabelIgnoreCase(String label);

	List<FeedbackCategory> findAllByOrderByLabel();

	List<FeedbackCategory> findAllByIsActiveOrderByLabel(boolean isActive);

	boolean existsByLabelIgnoreCaseAndFeedbackCategoryIdNot(String label, int id);
}