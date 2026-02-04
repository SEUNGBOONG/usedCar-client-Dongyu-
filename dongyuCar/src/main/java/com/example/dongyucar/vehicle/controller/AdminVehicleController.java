package com.example.dongyucar.vehicle.controller;

import com.example.dongyucar.vehicle.dto.request.VehicleRequestDto;
import com.example.dongyucar.vehicle.dto.response.VehicleDetailResponseDto;
import com.example.dongyucar.vehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/vehicles")
@RequiredArgsConstructor
public class AdminVehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public Long create(@ModelAttribute VehicleRequestDto dto) throws Exception {
        return vehicleService.createVehicle(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
    }

    @PutMapping("/{id}")
    public VehicleDetailResponseDto update(
            @PathVariable Long id,
            @ModelAttribute VehicleRequestDto dto
    ) throws Exception {
        return vehicleService.updateVehicle(id, dto);
    }

    @DeleteMapping("/{vehicleId}/images/{imageId}")
    public void deleteImage(
            @PathVariable Long vehicleId,
            @PathVariable Long imageId
    ) {
        vehicleService.deleteImage(vehicleId, imageId);
    }
}
