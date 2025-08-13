package com.example.dongyucar.management.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ManagementResponse {

    private Long id;
    private String siteName;
    private String customerName;
    private String customerPhone;
    private String desiredModel;

    @JsonProperty("isContacted")
    private boolean contacted;

    private String notes;
}
