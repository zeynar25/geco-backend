package com.example.geco.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentSettingsUpdateRequest {

    @Size(max = 20, message = "GCash number must not exceed 20 characters.")
    private String gcashNumber;

    @Size(max = 150, message = "GCash account name must not exceed 150 characters.")
    private String gcashAccountName;

    @Size(max = 2000, message = "Instructions must not exceed 2000 characters.")
    private String instructions;
}