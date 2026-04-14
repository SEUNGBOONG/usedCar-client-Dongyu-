package com.example.dongyucar.lease.controller.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "vehicleId와 type이 경로/쿼리스트링으로 이미 정해진 경우 사용하는 요청")
public class LeaseInfoDataRequest {

    @NotNull
    @Schema(description = "리스/렌트 상세 데이터 JSON", example = "{\"monthlyFee\":420000,\"deposit\":3000000}")
    private JsonNode data;
}
