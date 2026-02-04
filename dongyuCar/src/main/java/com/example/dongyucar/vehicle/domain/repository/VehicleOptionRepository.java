package com.example.dongyucar.vehicle.domain.repository;

import com.example.dongyucar.vehicle.domain.entity.VehicleOption;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VehicleOptionRepository extends JpaRepository<VehicleOption, Long> {

    @Query("SELECT o FROM VehicleOption o WHERE o.vehicle.id = :vehicleId")
    List<VehicleOption> findByVehicleId(@Param("vehicleId") Long vehicleId);
}
