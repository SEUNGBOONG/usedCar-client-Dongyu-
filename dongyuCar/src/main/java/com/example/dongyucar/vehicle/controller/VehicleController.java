package com.example.dongyucar.vehicle.controller;

import com.example.dongyucar.vehicle.dto.response.VehicleDetailResponseDto;
import com.example.dongyucar.vehicle.dto.response.VehicleResponseDto;
import com.example.dongyucar.vehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public Page<VehicleResponseDto> list(@RequestParam(defaultValue = "0") int page) {
        return vehicleService.getVehiclePage(page);
    }

    @GetMapping("/{id}")
    public VehicleDetailResponseDto detail(@PathVariable Long id) {
        return vehicleService.getVehicle(id);
    }

    @GetMapping("/search")
    public Page<VehicleResponseDto> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page
    ) {
        return vehicleService.searchVehicles(keyword, page);
    }
}
