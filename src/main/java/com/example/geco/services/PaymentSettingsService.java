package com.example.geco.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.geco.domains.PaymentSettings;
import com.example.geco.dto.PaymentSettingsUpdateRequest;
import com.example.geco.repositories.PaymentSettingsRepository;
import com.example.geco.utils.ImageUtils;

@Service
@Transactional
public class PaymentSettingsService extends BaseService {

    @Value("${app.upload-dir.payment-settings:C:/sts-4.32.0.RELEASE/dev/geco/uploads/payment-settings}")
    private String paymentSettingsUploadDir;

    @Value("${app.storage.bucket.geco_qr:}")
    private String paymentSettingsBucket;

    @Autowired
    private PaymentSettingsRepository paymentSettingsRepository;

    @Autowired
    private StorageService storageService;

    @Transactional(readOnly = true)
    public PaymentSettings getActiveSettings() {
        return paymentSettingsRepository.findFirstByIsActiveTrueOrderByUpdatedAtDesc()
                .orElseGet(() -> paymentSettingsRepository.save(
                        PaymentSettings.builder()
                                .gcashNumber("09XXXXXXXXX")
                                .gcashAccountName("CvSU Agri-Eco Park")
                                .instructions("After admin approval, submit your proof of payment.")
                                .isActive(true)
                                .build()
                ));
    }

    public PaymentSettings updateSettings(PaymentSettingsUpdateRequest request, MultipartFile gcashQrFile) {
        if (request == null) {
            throw new IllegalArgumentException("No fields provided to update payment settings.");
        }

        boolean hasJsonChanges =
                request.getGcashNumber() != null ||
                request.getGcashAccountName() != null ||
                request.getInstructions() != null;

        boolean hasFile = gcashQrFile != null && !gcashQrFile.isEmpty();

        if (!hasJsonChanges && !hasFile) {
            throw new IllegalArgumentException("No fields provided to update payment settings.");
        }

        PaymentSettings settings = getActiveSettings();

        if (request.getGcashNumber() != null) {
            settings.setGcashNumber(normalizeAndValidateGcashNumber(request.getGcashNumber()));
        }

        if (request.getGcashAccountName() != null) {
            String accountName = request.getGcashAccountName().trim();
            if (accountName.isBlank()) {
                throw new IllegalArgumentException("GCash account name cannot be blank.");
            }
            settings.setGcashAccountName(accountName);
        }

        if (request.getInstructions() != null) {
            settings.setInstructions(request.getInstructions().trim());
        }

        if (hasFile) {
            String qrUrl = uploadQrImage(settings.getId(), gcashQrFile);
            settings.setGcashQrImage(qrUrl);
        }

        return paymentSettingsRepository.save(settings);
    }

    private String normalizeAndValidateGcashNumber(String raw) {
        String digits = raw == null ? "" : raw.replaceAll("[^0-9]", "");

        if (digits.startsWith("63") && digits.length() == 12) {
            digits = "0" + digits.substring(2); // 639XXXXXXXXX -> 09XXXXXXXXX
        } else if (digits.length() == 10 && digits.startsWith("9")) {
            digits = "0" + digits; // 9XXXXXXXXX -> 09XXXXXXXXX
        }

        if (!digits.matches("^09\\d{9}$")) {
            throw new IllegalArgumentException("Invalid GCash number format. Use 09XXXXXXXXX.");
        }

        return digits;
    }

    private String uploadQrImage(Integer settingsId, MultipartFile file) {
        String originalName = file.getOriginalFilename();
        String ext = ".jpg";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }

        String fileName = "gcash-qr-" + settingsId + ext;
        boolean saved = false;
        String finalUrl = null;

        if (paymentSettingsBucket != null && !paymentSettingsBucket.isBlank()) {
            try {
                String contentType = file.getContentType() == null
                        ? "application/octet-stream"
                        : file.getContentType();

                boolean isImage = contentType.startsWith("image/");
                if (isImage) {
                    byte[] compressed = ImageUtils.compressImage(file, 0.85f, 1600, 1600);
                    finalUrl = storageService.upload(
                            new ByteArrayInputStream(compressed),
                            "image/jpeg",
                            paymentSettingsBucket,
                            fileName
                    );
                    saved = true;
                } else {
                    try (InputStream input = file.getInputStream()) {
                        finalUrl = storageService.upload(
                                input,
                                contentType,
                                paymentSettingsBucket,
                                fileName
                        );
                        saved = true;
                    }
                }
            } catch (IOException ignored) {
                // fallback to local below
            }
        }

        if (!saved) {
            try {
                Path uploadPath = Paths.get(paymentSettingsUploadDir);
                Files.createDirectories(uploadPath);

                Path target = uploadPath.resolve(fileName);
                String contentType = file.getContentType() == null
                        ? "application/octet-stream"
                        : file.getContentType();

                if (contentType.startsWith("image/")) {
                    byte[] compressed = ImageUtils.compressImage(file, 0.85f, 1600, 1600);
                    Files.write(target, compressed);
                } else {
                    file.transferTo(target.toFile());
                }

                finalUrl = "/uploads/payment-settings/" + fileName;
            } catch (IOException e) {
                throw new RuntimeException("Failed to save GCash QR image", e);
            }
        }

        return finalUrl;
    }
}