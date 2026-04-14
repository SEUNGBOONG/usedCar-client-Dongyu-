package com.example.dongyucar.lease.controller.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "lease_info 응답")
public record LeaseInfoResponse(
        @Schema(description = "lease_info 테이블 PK", example = "10")
        Long id,
        @Schema(description = "vehicle 테이블 PK", example = "123")
        Long vehicleId,
        @Schema(description = "상품 유형", example = "LEASE")
        String type,
        @Schema(description = "리스/렌트 상세 데이터 JSON")
        JsonNode data,
        @Schema(description = "생성 시각")
        LocalDateTime createdAt,
        @Schema(description = "수정 시각")
        LocalDateTime updatedAt
) {
}
