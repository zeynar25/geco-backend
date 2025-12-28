package com.example.geco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttractionResponse {
    private int attractionId;
    private String name;
    private String description;
    private String funFact;
    private String photo2dUrl;
    private boolean isActive;
}