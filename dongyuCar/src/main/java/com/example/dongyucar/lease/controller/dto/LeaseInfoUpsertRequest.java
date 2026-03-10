package com.example.dongyucar.lease.controller.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LeaseInfoUpsertRequest {

    @NotNull
    private Long vehicleId;

    @NotBlank
    private String type; // "RENT" | "LEASE"

    @NotNull
    private JsonNode data;
}

