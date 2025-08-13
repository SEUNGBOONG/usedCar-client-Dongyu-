package com.example.dongyucar.management.domain.repository;

import com.example.dongyucar.management.domain.entity.Management;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManagementRepository extends JpaRepository<Management, Long> {
    Page<Management> findAllBySiteName(String siteName, Pageable pageable);
}
