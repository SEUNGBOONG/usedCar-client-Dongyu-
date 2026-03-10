package com.example.dongyucar.lease.controller;

import com.example.dongyucar.lease.controller.dto.LeaseInfoResponse;
import com.example.dongyucar.lease.controller.dto.LeaseInfoUpsertRequest;
import com.example.dongyucar.lease.service.LeaseInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lease-info")
@RequiredArgsConstructor
public class LeaseInfoController {

    private final LeaseInfoService service;

    // 프론트에서 값 "담기만" 하면 되는 upsert
    @PostMapping
    public LeaseInfoResponse upsert(@Valid @RequestBody LeaseInfoUpsertRequest req) {
        return service.upsert(req);
    }

    // 예: /lease-info?vehicleId=1&type=LEASE
    @GetMapping
    public LeaseInfoResponse get(@RequestParam Long vehicleId, @RequestParam String type) {
        return service.get(vehicleId, type);
    }

    @GetMapping("/{id}")
    public LeaseInfoResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }
}

