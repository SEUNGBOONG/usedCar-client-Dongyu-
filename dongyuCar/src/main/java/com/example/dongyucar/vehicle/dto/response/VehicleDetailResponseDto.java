package com.example.dongyucar.vehicle.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class VehicleDetailResponseDto {

    private Long id;
    private String title;
    private String model;
    private String year;
    private Integer mileage;
    private Integer price;
    private Integer monthFee;
    private Integer supportFee;

    // 🔥 [추가됨] 상세 조회 시 설명 반환
    private String description;

    private String color;
    private String fuelType;
    private String gearType;
    private Integer accidentHistory;

    private List<String> images;
    private List<String> options;
}
