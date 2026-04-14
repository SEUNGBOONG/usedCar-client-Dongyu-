package com.example.dongyucar.lease.service;

import com.example.dongyucar.common.NotFoundException;
import com.example.dongyucar.lease.controller.dto.LeaseInfoDataRequest;
import com.example.dongyucar.lease.controller.dto.LeaseInfoResponse;
import com.example.dongyucar.lease.controller.dto.LeaseInfoUpsertRequest;
import com.example.dongyucar.lease.controller.dto.LeaseInfoVehicleUpsertRequest;
import com.example.dongyucar.lease.domain.entity.LeaseInfo;
import com.example.dongyucar.lease.domain.repository.LeaseInfoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaseInfoService {

    private final LeaseInfoRepository repository;
    private final ObjectMapper objectMapper;

    public LeaseInfoResponse create(LeaseInfoUpsertRequest req) {
        String type = normalizeType(req.getType());
        String payload = toJson(req.getData());

        repository.findByVehicleIdAndType(req.getVehicleId(), type)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("LeaseInfo already exists: vehicleId=" + req.getVehicleId() + ", type=" + type);
                });

        LeaseInfo saved = repository.save(
                LeaseInfo.builder()
                        .vehicleId(req.getVehicleId())
                        .type(type)
                        .payloadJson(payload)
                        .build()
        );
        return toRes(saved);
    }

    public LeaseInfoResponse upsert(LeaseInfoUpsertRequest req) {
        String type = normalizeType(req.getType());
        String payload = toJson(req.getData());

        LeaseInfo entity = repository.findByVehicleIdAndType(req.getVehicleId(), type)
                .orElseGet(() -> LeaseInfo.builder()
                        .vehicleId(req.getVehicleId())
                        .type(type)
                        .payloadJson("{}")
                        .build());

        entity.setPayloadJson(payload);

        LeaseInfo saved = repository.save(entity);
        return toRes(saved);
    }

    public LeaseInfoResponse upsertByVehicle(Long vehicleId, LeaseInfoVehicleUpsertRequest req) {
        String type = normalizeType(req.getType());
        String payload = toJson(req.getData());

        LeaseInfo entity = repository.findByVehicleIdAndType(vehicleId, type)
                .orElseGet(() -> LeaseInfo.builder()
                        .vehicleId(vehicleId)
                        .type(type)
                        .payloadJson("{}")
                        .build());

        entity.setVehicleId(vehicleId);
        entity.setType(type);
        entity.setPayloadJson(payload);

        LeaseInfo saved = repository.save(entity);
        return toRes(saved);
    }

    public LeaseInfoResponse update(Long id, LeaseInfoUpsertRequest req) {
        LeaseInfo entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("LeaseInfo not found: id=" + id));

        entity.setVehicleId(req.getVehicleId());
        entity.setType(normalizeType(req.getType()));
        entity.setPayloadJson(toJson(req.getData()));

        LeaseInfo saved = repository.save(entity);
        return toRes(saved);
    }

    public void deleteById(Long id) {
        LeaseInfo entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("LeaseInfo not found: id=" + id));
        repository.delete(entity);
    }

    public void delete(Long vehicleId, String type) {
        String normalized = normalizeType(type);
        LeaseInfo entity = repository.findByVehicleIdAndType(vehicleId, normalized)
                .orElseThrow(() -> new NotFoundException("LeaseInfo not found: vehicleId=" + vehicleId + ", type=" + normalized));
        repository.delete(entity);
    }

    @Transactional(readOnly = true)
    public LeaseInfoResponse get(Long vehicleId, String type) {
        String normalized = normalizeType(type);
        LeaseInfo entity = repository.findByVehicleIdAndType(vehicleId, normalized)
                .orElseThrow(() -> new NotFoundException("LeaseInfo not found: vehicleId=" + vehicleId + ", type=" + normalized));
        return toRes(entity);
    }

    @Transactional(readOnly = true)
    public LeaseInfoResponse getById(Long id) {
        return repository.findById(id)
                .map(this::toRes)
                .orElseThrow(() -> new NotFoundException("LeaseInfo not found: id=" + id));
    }

    public LeaseInfoResponse updateDataByVehicle(Long vehicleId, String type, LeaseInfoDataRequest req) {
        String normalized = normalizeType(type);
        LeaseInfo entity = repository.findByVehicleIdAndType(vehicleId, normalized)
                .orElseThrow(() -> new NotFoundException("LeaseInfo not found: vehicleId=" + vehicleId + ", type=" + normalized));

        entity.setPayloadJson(toJson(req.getData()));

        LeaseInfo saved = repository.save(entity);
        return toRes(saved);
    }

    private LeaseInfoResponse toRes(LeaseInfo entity) {
        return new LeaseInfoResponse(
                entity.getId(),
                entity.getVehicleId(),
                entity.getType(),
                toJsonNode(entity.getPayloadJson()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private String normalizeType(String type) {
        if (type == null) throw new IllegalArgumentException("type is required");
        String t = type.trim().toUpperCase();
        if (t.equals("LENT")) {
            return "RENT";
        }
        if (!t.equals("RENT") && !t.equals("LEASE")) {
            throw new IllegalArgumentException("type must be RENT, LENT or LEASE");
        }
        return t;
    }

    private String toJson(JsonNode node) {
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid data JSON", e);
        }
    }

    private JsonNode toJsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Corrupted payload JSON in DB", e);
        }
    }
}
