package com.example.dongyucar.management.service;

import com.example.dongyucar.management.controller.dto.AdminUpdateRequest;
import com.example.dongyucar.management.controller.dto.ClientCreateRequest;
import com.example.dongyucar.management.controller.dto.ManagementResponse;
import com.example.dongyucar.management.domain.entity.Management;
import com.example.dongyucar.management.domain.repository.ManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ManagementService {

    private final ManagementRepository repository;

    @Value("${management.site-name}")
    private String siteNameFixed;

    public ManagementResponse createByClient(ClientCreateRequest req) {
        Management m = Management.builder()
                .siteName(siteNameFixed)
                .customerName(req.getCustomerName())
                .customerPhone(req.getCustomerPhone())
                .desiredModel(req.getDesiredModel())
                .isContacted(false)
                .notes(null)
                .build();
        return toRes(repository.save(m));
    }

    public ManagementResponse updateAdmin(Long id, AdminUpdateRequest req) {
        Management m = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + id));

        if (req.getNotes() != null) m.setNotes(req.getNotes());
        if (req.getContacted() != null) m.setContacted(req.getContacted());

        return toRes(repository.save(m));
    }

    @Transactional(readOnly = true)
    public ManagementResponse get(Long id) {
        return repository.findById(id)
                .map(this::toRes)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ManagementResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(this::toRes);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private ManagementResponse toRes(Management m) {
        return new ManagementResponse(
                m.getId(),
                m.getSiteName(),
                m.getCustomerName(),
                m.getCustomerPhone(),
                m.getDesiredModel(),
                m.isContacted(),
                m.getNotes()
        );
    }
}
