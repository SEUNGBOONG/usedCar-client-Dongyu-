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
    private String supportFeeType;

    // 🔥 [추가됨] 차량 설명 입력 필드
    private String description;

    private String color;
    private String fuelType;
    private String gearType;

    private List<MultipartFile> images;
    private List<String> options;

    // ---- Compatibility fields (form-data / legacy clients) ----
    // Some clients send snake_case or different names; bind and normalize here.
    private String content;          // = description
    private String fuel_type;        // = fuelType
    private String gear_type;        // = gearType
    private Integer month_fee;       // = monthFee
    private Integer support_fee;     // = supportFee
    private String support_fee_type; // = supportFeeType

    public void setContent(String content) {
        this.content = content;
        if (this.description == null) this.description = content;
    }

    public void setFuel_type(String fuelType) {
        this.fuel_type = fuelType;
        if (this.fuelType == null) this.fuelType = fuelType;
    }

    public void setGear_type(String gearType) {
        this.gear_type = gearType;
        if (this.gearType == null) this.gearType = gearType;
    }

    public void setMonth_fee(Integer monthFee) {
        this.month_fee = monthFee;
        if (this.monthFee == null) this.monthFee = monthFee;
    }

    public void setSupport_fee(Integer supportFee) {
        this.support_fee = supportFee;
        if (this.supportFee == null) this.supportFee = supportFee;
    }

    public void setSupport_fee_type(String supportFeeType) {
        this.support_fee_type = supportFeeType;
        if (this.supportFeeType == null) this.supportFeeType = supportFeeType;
    }
}
