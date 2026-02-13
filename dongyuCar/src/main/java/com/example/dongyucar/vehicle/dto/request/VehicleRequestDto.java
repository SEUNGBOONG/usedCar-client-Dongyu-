package com.example.dongyucar.vehicle.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
public class VehicleRequestDto {

    private String title;
    private String model;
    private String year;
    private Integer mileage;
    private Integer price;
    private Integer monthFee;
    private Integer supportFee;

    // 🔥 [추가됨] 차량 설명 입력 필드
    private String description;

    private String color;
    private String fuelType;
    private String gearType;
    private Integer accidentHistory;

    private List<MultipartFile> images;
    private List<String> options;
}
