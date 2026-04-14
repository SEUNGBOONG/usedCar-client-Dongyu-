package com.example.dongyucar.vehicle.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;       // 차량 제목
    private String model;       // 차량 모델명
    private String year;        // 연식
    private Integer mileage;    // 주행거리
    private Integer price;      // 차량가격
    private Integer monthFee;   // 월 렌트료
    private Integer supportFee; // 승계지원금
    private String supportFeeType; // SUPPORT | TAKEOVER

    // 🔥 [추가됨] 차량 설명 (본문)
    @Column(length = 2000) // 길이를 넉넉하게 잡거나, columnDefinition="TEXT" 사용 권장
    private String description;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleOption> options = new ArrayList<>();

    @OneToOne(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private VehicleDetail detail;

    public void addImage(VehicleImage img) {
        images.add(img);
        img.setVehicle(this);
    }

    public void addOption(VehicleOption option) {
        options.add(option);
        option.setVehicle(this);
    }
}
