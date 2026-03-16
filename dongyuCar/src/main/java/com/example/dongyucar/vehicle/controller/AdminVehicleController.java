package com.example.dongyucar.vehicle.controller;

import com.example.dongyucar.vehicle.dto.request.VehicleRequestDto;
import com.example.dongyucar.vehicle.dto.response.VehicleDetailResponseDto;
import com.example.dongyucar.vehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/vehicles")
@RequiredArgsConstructor
public class AdminVehicleController {

    private final VehicleService vehicleService;

    // 차량 등록 - 멀티파트 데이터 명시
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long create(
            @RequestParam MultiValueMap<String, String> form,
            @RequestPart(required = false) List<MultipartFile> images
    ) throws Exception {
        return vehicleService.createVehicle(toDto(form, images));
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
            @RequestParam MultiValueMap<String, String> form,
            @RequestPart(required = false) List<MultipartFile> images
    ) throws Exception {
        return vehicleService.updateVehicle(id, toDto(form, images));
    }

    // 이미지 개별 삭제
    @DeleteMapping("/{vehicleId}/images/{imageId}")
    public void deleteImage(
            @PathVariable Long vehicleId,
            @PathVariable Long imageId
    ) {
        vehicleService.deleteImage(vehicleId, imageId);
    }

    private VehicleRequestDto toDto(MultiValueMap<String, String> form, List<MultipartFile> images) {
        VehicleRequestDto dto = new VehicleRequestDto();

        dto.setTitle(first(form, "title"));
        dto.setModel(first(form, "model"));
        dto.setYear(first(form, "year"));
        dto.setMileage(firstInt(form, "mileage"));
        dto.setPrice(firstInt(form, "price"));
        dto.setMonthFee(firstInt(form, "monthFee", "month_fee"));
        dto.setSupportFee(firstInt(form, "supportFee", "support_fee"));
        dto.setDescription(first(form, "description", "desc", "contents", "content"));

        dto.setColor(first(form, "color"));
        dto.setFuelType(first(form, "fuelType", "fuel_type", "fuel"));
        dto.setGearType(first(form, "gearType", "gear_type", "gear"));
        dto.setAccidentHistory(firstInt(form, "accidentHistory", "accident_history"));

        List<String> options = form.get("options");
        dto.setOptions(options);
        dto.setImages(images);

        return dto;
    }

    private static String first(MultiValueMap<String, String> form, String... keys) {
        for (String key : keys) {
            String v = form.getFirst(key);
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    private static Integer firstInt(MultiValueMap<String, String> form, String... keys) {
        String v = first(form, keys);
        if (v == null) return null;
        try {
            return Integer.valueOf(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
