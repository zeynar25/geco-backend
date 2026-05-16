package com.example.geco.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.geco.domains.PaymentSettings;
import com.example.geco.dto.PaymentSettingsUpdateRequest;
import com.example.geco.services.PaymentSettingsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/payment-settings")
@Tag(name = "Payment Settings Controller", description = "Manage payment details such as GCash number and QR image")
public class PaymentSettingsController extends AbstractController {

    @Autowired
    private PaymentSettingsService paymentSettingsService;

    @Operation(
        summary = "Get active payment settings",
        description = "Retrieve active payment settings for public booking pages (e.g., GCash number/account name/QR)."
    )
    @GetMapping("/active")
    public ResponseEntity<PaymentSettings> getActivePaymentSettings() {
        return ResponseEntity.ok(paymentSettingsService.getActiveSettings());
    }

    @Operation(
        summary = "Update payment settings (Staff/Admin)",
        description = "Update payment settings and optionally upload a GCash QR image."
    )
    @PatchMapping(
        value = "/staff",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<PaymentSettings> updatePaymentSettings(
        @Parameter(description = "Payment settings update fields (JSON)")
        @RequestPart("data") @Valid PaymentSettingsUpdateRequest request,

        @Parameter(description = "GCash QR image (optional)")
        @RequestPart(value = "gcashQr", required = false) MultipartFile gcashQr
    ) {
        return ResponseEntity.ok(paymentSettingsService.updateSettings(request, gcashQr));
    }
}