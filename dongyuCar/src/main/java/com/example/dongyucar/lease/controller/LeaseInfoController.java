package com.example.dongyucar.lease.controller;

import com.example.dongyucar.lease.controller.dto.LeaseInfoResponse;
import com.example.dongyucar.lease.controller.dto.LeaseInfoUpsertRequest;
import com.example.dongyucar.lease.service.LeaseInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/lease-info")
@RequiredArgsConstructor
public class LeaseInfoController {

    private final LeaseInfoService service;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public LeaseInfoResponse create(@Valid @RequestBody LeaseInfoUpsertRequest req) {
        return service.create(req);
    }

    // 기존 클라이언트 호환용 upsert
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

    @PutMapping("/{id}")
    public LeaseInfoResponse update(@PathVariable Long id, @Valid @RequestBody LeaseInfoUpsertRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam Long vehicleId, @RequestParam String type) {
        service.delete(vehicleId, type);
    }
}

