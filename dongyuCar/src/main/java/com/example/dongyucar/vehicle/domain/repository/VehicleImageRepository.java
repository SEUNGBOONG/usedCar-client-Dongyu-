package com.example.dongyucar.vehicle.domain.repository;

import com.example.dongyucar.vehicle.domain.entity.VehicleImage;
import org.springframework.data.jpa.repository.JpaRepository;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VehicleImageRepository extends JpaRepository<VehicleImage, Long> {

    @Query("SELECT i FROM VehicleImage i WHERE i.vehicle.id = :vehicleId")
    List<VehicleImage> findByVehicleId(@Param("vehicleId") Long vehicleId);
}
