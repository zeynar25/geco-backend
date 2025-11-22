package com.example.geco.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.Faq;
import com.example.geco.dto.FaqOrderRequest;

@RestController
@RequestMapping("/faq")
public class FaqController extends AbstractController {
	@PostMapping
	public ResponseEntity<Faq> addFaq(@RequestBody Faq faq) {
		Faq savedFaq = faqService.addFaq(faq);
        return new ResponseEntity<>(savedFaq, HttpStatus.CREATED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Faq> getFaq(@PathVariable int id) {
		Faq faq = faqService.getFaq(id);
        return new ResponseEntity<>(faq, HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<List<Faq>> getAllFaqs() {
		List<Faq> faqs = faqService.getAllFaqs();
        return new ResponseEntity<>(faqs, HttpStatus.OK);
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<Faq> updateFaq(@PathVariable int id, @RequestBody Faq faq) {
		faq.setFaqId(id);
		Faq updatedFaq = faqService.updateFaq(faq);
        return new ResponseEntity<>(updatedFaq, HttpStatus.OK);
	}
	
	@PostMapping("/reorder")
	public ResponseEntity<List<Faq>> reorderFaqs(@RequestBody List<FaqOrderRequest> orderList) {
	    faqService.updateOrder(orderList);
	    return ResponseEntity.ok(faqService.getAllFaqs());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Faq> deleteFaq(@PathVariable int id) {
		faqService.deleteFaq(id);
	    return ResponseEntity.noContent().build();
	}
}
