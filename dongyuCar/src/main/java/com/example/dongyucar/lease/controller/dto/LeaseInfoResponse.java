package com.example.dongyucar.lease.controller.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public record LeaseInfoResponse(
        Long id,
        Long vehicleId,
        String type,
        JsonNode data,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

