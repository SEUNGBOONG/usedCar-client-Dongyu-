package com.example.dongyucar.lease.domain.repository;

import com.example.dongyucar.lease.domain.entity.LeaseInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaseInfoRepository extends JpaRepository<LeaseInfo, Long> {
    Optional<LeaseInfo> findByVehicleIdAndType(Long vehicleId, String type);
}

