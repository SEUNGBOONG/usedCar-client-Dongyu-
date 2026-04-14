package com.example.dongyucar.lease.controller.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "vehicleId를 경로로 전달할 때 사용하는 생성/수정 요청")
public class LeaseInfoVehicleUpsertRequest {

    @NotBlank
    @Schema(description = "상품 유형", allowableValues = {"RENT", "LEASE", "LENT"})
    private String type;

    @NotNull
    @Schema(description = "리스/렌트 상세 데이터 JSON", example = "{\"monthlyFee\":420000,\"deposit\":3000000}")
    private JsonNode data;
}
