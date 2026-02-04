package com.example.dongyucar.vehicle.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VehicleResponseDto {

    private Long id;
    private String title;
    private String thumbnail;

    // 🔥 추가 부분
    private String year;
    private Integer price;
    private Integer mileage;
    private String fuelType;
    private String gearType;
    private String color;
}
