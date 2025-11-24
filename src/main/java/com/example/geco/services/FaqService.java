package com.example.geco.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.geco.domains.Account;
import com.example.geco.domains.Faq;
import com.example.geco.domains.AuditLog.LogAction;
import com.example.geco.dto.FaqOrderRequest;
import com.example.geco.repositories.FaqRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class FaqService extends BaseService{
	@Autowired
	FaqRepository faqRepository;
	
	public Faq createFaqCopy(Faq faq) {
		return Faq.builder()
				.faqId(faq.getFaqId())
				.question(faq.getQuestion())
				.answer(faq.getAnswer())
				.isActive(faq.isActive())
				.displayOrder(faq.getDisplayOrder())
				.build();
	}
	
	public Faq addFaq(Faq faq) {
		if (faq.getQuestion() == null || faq.getQuestion().isBlank()) {
	        throw new IllegalArgumentException("Question is missing.");
	    }
		
		if (faq.getAnswer() == null || faq.getAnswer().isBlank()) {
	        throw new IllegalArgumentException("Answer is missing.");
		}

		faq.setQuestion(faq.getQuestion().trim());
		if (faq.getQuestion().length() < 10) {
	        throw new IllegalArgumentException("Question must have at least 10 characters.");
	    }
		
		if (faqRepository.existsByQuestionIgnoreCase(faq.getQuestion())) {
			throw new IllegalArgumentException("Question \"" + faq.getQuestion() + "\" already exists.");
		}

		faq.setAnswer(faq.getAnswer().trim());
		if (faq.getAnswer().length() < 10) {
	        throw new IllegalArgumentException("Answer must have at least 10 characters.");
	    }
		
		int nextOrder = faqRepository.getMaxDisplayOrderForUpdate() + 1;
	    faq.setDisplayOrder(nextOrder);
		
		Faq savedFaq = faqRepository.save(faq);
		
		logIfStaffOrAdmin("Faq", (long)savedFaq.getFaqId(), LogAction.CREATE, null, savedFaq);
		
		return savedFaq;
	}
	
	public Faq getFaq(int id) {
		return faqRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("FAQ with ID '" + id + "' not found."));
	}
	
	public List<Faq> getAllFaqs() {
		return faqRepository.findAllByOrderByDisplayOrder();
	}
	
	public Faq updateFaq(Faq faq) {
		if (faq.getQuestion() == null && faq.getAnswer() == null) {
			throw new IllegalArgumentException("FAQ question and answer is empty.");
		}
		
		Faq existingFaq = faqRepository.findById(faq.getFaqId())
	            .orElseThrow(() -> new EntityNotFoundException("FAQ with ID \"" + faq.getFaqId() + "\" not found."));
		
		
		Faq prevFaq = createFaqCopy(existingFaq);
		
		if (faq.getQuestion() != null) {
	        String newQuestion = faq.getQuestion().trim();

			// Check if it duplicates another record.
			// New question is not equal to the existing question of the id
			// And this question does not exist in FAQ records.
	        if (!existingFaq.getQuestion().equalsIgnoreCase(newQuestion) &&
	                faqRepository.existsByQuestionIgnoreCase(newQuestion)) {
	            throw new IllegalArgumentException("Another FAQ with the same question already exists.");
	        }

	        existingFaq.setQuestion(newQuestion);
	    }
		
		if (faq.getAnswer() != null) {
		    existingFaq.setAnswer(faq.getAnswer().trim());
		}
		
		logIfStaffOrAdmin("Faq", (long)existingFaq.getFaqId(), LogAction.UPDATE, prevFaq, existingFaq);
		
		return faqRepository.save(existingFaq);
	}

	@Transactional
	public void updateOrder(List<FaqOrderRequest> orderList) {
		List<Faq> prevOrderList = getAllFaqs();
		
		for (FaqOrderRequest item : orderList) {
	        Faq faq = faqRepository.findById(item.getFaqId())
	                .orElseThrow(() -> new EntityNotFoundException("FAQ with ID '" + item.getFaqId() + "' not found."));
	        faq.setDisplayOrder(item.getDisplayOrder());
	    }
		
		List<Faq> updatedOrderList = getAllFaqs();
		
		logIfStaffOrAdmin("Faq", null, LogAction.DISABLE, prevOrderList, updatedOrderList);
	}
	
	public void softDeleteFaq(int id) {
		Faq faq = faqRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("FAQ with ID '" + id + "' not found."));
		
		if (!faq.isActive()) {
	        throw new IllegalStateException("FAQ is already disabled.");
	    }
		
		Faq prevFaq = createFaqCopy(faq);
		
		faq.setActive(false);
		
		logIfStaffOrAdmin("Faq", (long)faq.getFaqId(), LogAction.DISABLE, prevFaq, faq);
		
		faqRepository.save(faq);
	}
	
	public void hardDeleteFaq(int id) {
		Faq faq = faqRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("FAQ with ID '" + id + "' not found."));
	    
		logIfStaffOrAdmin("Faq", (long)faq.getFaqId(), LogAction.DELETE, faq, null);
		
		faqRepository.delete(faq);
	}

	public void restoreFaq(int id) {
		Faq faq = faqRepository.findById(id)
		        .orElseThrow(() -> new EntityNotFoundException("Faq ID '" + id + "' not found."));

	    if (faq.isActive()) {
	        throw new IllegalStateException("FAQ is already active.");
	    }
	    
	    Faq prevFaq = createFaqCopy(faq);

	    faq.setActive(true);
	    faqRepository.save(faq);
	    
	    logIfStaffOrAdmin("Faq", (long) id, LogAction.RESTORE, prevFaq, faq);
	}
}
