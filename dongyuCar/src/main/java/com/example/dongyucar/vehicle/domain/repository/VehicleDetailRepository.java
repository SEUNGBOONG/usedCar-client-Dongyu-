package com.example.dongyucar.vehicle.domain.repository;

import com.example.dongyucar.vehicle.domain.entity.VehicleDetail;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VehicleDetailRepository extends JpaRepository<VehicleDetail, Long> {

    @Query("SELECT d FROM VehicleDetail d WHERE d.vehicle.id = :vehicleId")
    Optional<VehicleDetail> findByVehicleId(@Param("vehicleId") Long vehicleId);
}
