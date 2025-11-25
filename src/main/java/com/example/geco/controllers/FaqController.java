package com.example.geco.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.geco.domains.Faq;
import com.example.geco.dto.FaqOrderRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/faq")
@Tag(name = "FAQ Controller", description = "Manage FAQs, including adding, updating, reordering, and deleting")
public class FaqController extends AbstractController {

    @Operation(summary = "Add a new FAQ", description = "Create a new FAQ with a question and answer")
    @PostMapping
    public ResponseEntity<Faq> addFaq(@RequestBody Faq faq) {
        Faq savedFaq = faqService.addFaq(faq);
        return new ResponseEntity<>(savedFaq, HttpStatus.CREATED);
    }

    @Operation(summary = "Get FAQ by ID", description = "Retrieve a single FAQ by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<Faq> getFaq(
            @Parameter(description = "ID of the FAQ to retrieve") @PathVariable int id) {
        Faq faq = faqService.getFaq(id);
        return new ResponseEntity<>(faq, HttpStatus.OK);
    }

    @Operation(summary = "Get all FAQs", description = "Retrieve a list of all FAQs")
    @GetMapping
    public ResponseEntity<List<Faq>> getAllFaqs() {
        List<Faq> faqs = faqService.getAllFaqs();
        return new ResponseEntity<>(faqs, HttpStatus.OK);
    }
    
    @Operation(summary = "Get all active FAQs", description = "Retrieve a list of all active FAQs")
    @GetMapping("/active")
    public ResponseEntity<List<Faq>> getAllActiveFaqs() {
        List<Faq> faqs = faqService.getAllActiveFaqs();
        return new ResponseEntity<>(faqs, HttpStatus.OK);
    }
    
    @Operation(summary = "Get all Inactive FAQs", description = "Retrieve a list of all inactive FAQs")
    @GetMapping("/inactive")
    public ResponseEntity<List<Faq>> getAllInactiveFaqs() {
        List<Faq> faqs = faqService.getAllInactiveFaqs();
        return new ResponseEntity<>(faqs, HttpStatus.OK);
    }

    @Operation(summary = "Update FAQ by ID", description = "Update question and/or answer of a FAQ by ID")
    @PatchMapping("/{id}")
    public ResponseEntity<Faq> updateFaq(
            @Parameter(description = "ID of the FAQ to update") @PathVariable int id,
            @RequestBody Faq faq) {
        faq.setFaqId(id);
        Faq updatedFaq = faqService.updateFaq(faq);
        return new ResponseEntity<>(updatedFaq, HttpStatus.OK);
    }

    @Operation(summary = "Reorder FAQs", description = "Update the display order of multiple FAQs at once")
    @PatchMapping("/reorder")
    public ResponseEntity<List<Faq>> reorderFaqs(
            @Parameter(description = "List of FAQ IDs with their new display orders") @RequestBody List<FaqOrderRequest> orderList) {
        faqService.updateOrder(orderList);
        return ResponseEntity.ok(faqService.getAllFaqs());
    }

    @Operation(summary = "Delete FAQ by ID", description = "Soft delete by default, hard delete if specified")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaq(
            @Parameter(description = "ID of the FAQ to delete") @PathVariable int id,
            @Parameter(description = "Soft delete by default; set false for hard delete") 
            @RequestParam(defaultValue = "true") boolean soft) {

        if (soft) {
            faqService.softDeleteFaq(id);
        } else {
            faqService.hardDeleteFaq(id);
        }

        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Restore FAQ by ID", description = "Restore a previously soft-deleted FAQ")
    @PatchMapping("/admin/restore/{id}")
    public ResponseEntity<Void> restoreFaq(
            @Parameter(description = "ID of the FAQ to restore") @PathVariable int id) {
        faqService.restoreFaq(id);
        return ResponseEntity.noContent().build();
    }
}
