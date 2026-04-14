package com.example.dongyucar.lease.controller;

import com.example.dongyucar.lease.controller.dto.LeaseInfoDataRequest;
import com.example.dongyucar.lease.controller.dto.LeaseInfoResponse;
import com.example.dongyucar.lease.controller.dto.LeaseInfoUpsertRequest;
import com.example.dongyucar.lease.controller.dto.LeaseInfoVehicleUpsertRequest;
import com.example.dongyucar.lease.service.LeaseInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/lease-info")
@RequiredArgsConstructor
@Tag(name = "lease-info-controller", description = "차량별 리스/렌트 상세 정보 API")
public class LeaseInfoController {

    private final LeaseInfoService service;

    @Operation(summary = "lease_info 생성", description = "vehicleId + type 조합으로 신규 데이터를 생성합니다. 이미 있으면 실패합니다.")
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public LeaseInfoResponse create(@Valid @RequestBody LeaseInfoUpsertRequest req) {
        return service.create(req);
    }

    @Operation(summary = "lease_info 업서트", description = "기존 클라이언트 호환용 API입니다. vehicleId + type 기준으로 있으면 수정, 없으면 생성합니다.")
    @PostMapping
    public LeaseInfoResponse upsert(@Valid @RequestBody LeaseInfoUpsertRequest req) {
        return service.upsert(req);
    }

    @Operation(summary = "차량 기준 lease_info 조회", description = "프론트 기본 조회 API입니다. vehicleId는 차량 ID이고 lease_info PK가 아닙니다.")
    @GetMapping
    public LeaseInfoResponse get(
            @Parameter(description = "vehicle 테이블 PK", example = "123") @RequestParam Long vehicleId,
            @Parameter(description = "상품 유형", example = "LEASE") @RequestParam String type
    ) {
        return service.get(vehicleId, type);
    }

    @Operation(summary = "lease_info PK 기준 조회", description = "여기서 path id는 vehicleId가 아니라 lease_info 테이블 PK입니다.")
    @GetMapping("/{id}")
    public LeaseInfoResponse getById(
            @Parameter(name = "id", description = "lease_info 테이블 PK", example = "10") @PathVariable("id") Long leaseInfoId
    ) {
        return service.getById(leaseInfoId);
    }

    @Operation(summary = "lease_info 수정", description = "기본은 lease_info PK 수정입니다. 기존 프론트처럼 path id와 body vehicleId가 같으면 vehicleId + type 기준으로 안전하게 수정합니다.")
    @PutMapping("/{id}")
    public LeaseInfoResponse update(
            @Parameter(name = "id", description = "lease_info 테이블 PK", example = "10") @PathVariable("id") Long leaseInfoId,
            @Valid @RequestBody LeaseInfoUpsertRequest req
    ) {
        return service.update(leaseInfoId, req);
    }

    @Operation(summary = "lease_info PK 기준 삭제", description = "path id는 vehicleId가 아니라 lease_info 테이블 PK입니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(
            @Parameter(name = "id", description = "lease_info 테이블 PK", example = "10") @PathVariable("id") Long leaseInfoId
    ) {
        service.deleteById(leaseInfoId);
    }

    @Operation(summary = "차량 기준 lease_info 삭제", description = "vehicleId + type 조합으로 삭제합니다.")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "vehicle 테이블 PK", example = "123") @RequestParam Long vehicleId,
            @Parameter(description = "상품 유형", example = "LEASE") @RequestParam String type
    ) {
        service.delete(vehicleId, type);
    }

    @Operation(summary = "차량 기준 lease_info 업서트", description = "프론트 권장 API입니다. path의 vehicleId가 차량 ID이며 body에는 type과 data만 보냅니다.")
    @PutMapping("/vehicle/{vehicleId}")
    public LeaseInfoResponse upsertByVehicle(
            @Parameter(description = "vehicle 테이블 PK", example = "123") @PathVariable Long vehicleId,
            @Valid @RequestBody LeaseInfoVehicleUpsertRequest req
    ) {
        return service.upsertByVehicle(vehicleId, req);
    }

    @Operation(summary = "차량 기준 lease_info 데이터만 수정", description = "이미 존재하는 vehicleId + type 데이터의 data만 수정합니다.")
    @PutMapping("/vehicle/{vehicleId}/data")
    public LeaseInfoResponse updateDataByVehicle(
            @Parameter(description = "vehicle 테이블 PK", example = "123") @PathVariable Long vehicleId,
            @Parameter(description = "상품 유형", example = "LEASE") @RequestParam String type,
            @Valid @RequestBody LeaseInfoDataRequest req
    ) {
        return service.updateDataByVehicle(vehicleId, type, req);
    }
}
