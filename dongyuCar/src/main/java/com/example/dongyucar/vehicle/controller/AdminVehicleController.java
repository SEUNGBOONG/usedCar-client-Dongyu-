package com.example.dongyucar.vehicle.controller;

import com.example.dongyucar.vehicle.dto.request.VehicleRequestDto;
import com.example.dongyucar.vehicle.dto.response.VehicleDetailResponseDto;
import com.example.dongyucar.vehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/vehicles")
@RequiredArgsConstructor
public class AdminVehicleController {

    private final VehicleService vehicleService;

    // 차량 등록 - 멀티파트 데이터 명시
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long create(@ModelAttribute VehicleRequestDto dto) throws Exception {
        return vehicleService.createVehicle(dto);
    }

    // 차량 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
    }

    // 차량 수정 - 멀티파트 데이터 명시
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VehicleDetailResponseDto update(
            @PathVariable Long id,
            @ModelAttribute VehicleRequestDto dto
    ) throws Exception {
        return vehicleService.updateVehicle(id, dto);
    }

    // 이미지 개별 삭제
    @DeleteMapping("/{vehicleId}/images/{imageId}")
    public void deleteImage(
            @PathVariable Long vehicleId,
            @PathVariable Long imageId
    ) {
        vehicleService.deleteImage(vehicleId, imageId);
    }
}
