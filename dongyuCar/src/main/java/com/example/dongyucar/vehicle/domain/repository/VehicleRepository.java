package com.example.dongyucar.vehicle.domain.repository;

import com.example.dongyucar.vehicle.domain.entity.Vehicle;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v FROM Vehicle v ORDER BY v.id DESC")
    Page<Vehicle> findPage(Pageable pageable);

    @Query("SELECT v FROM Vehicle v JOIN FETCH v.detail d WHERE v.id = :id")
    Optional<Vehicle> findDetail(@Param("id") Long id);

    @Query("SELECT v FROM Vehicle v WHERE v.model LIKE %:keyword% ORDER BY v.id DESC")
    Page<Vehicle> searchByModel(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT v FROM Vehicle v JOIN FETCH v.detail")
    Page<Vehicle> findPageWithDetail(Pageable pageable);
}
