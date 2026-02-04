package com.example.dongyucar.vehicle.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDetail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String color;
    private String fuelType;
    private String gearType;
    private Integer accidentHistory; // 무사고, 단순수리 등등

    @OneToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
}
