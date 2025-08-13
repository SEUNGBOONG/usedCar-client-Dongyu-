package com.example.dongyucar.management.controller;

import com.example.dongyucar.management.controller.dto.ContactRequest;
import com.example.dongyucar.management.util.ContactEmailUtil;
import com.example.dongyucar.management.controller.dto.AdminUpdateRequest;
import com.example.dongyucar.management.controller.dto.ClientCreateRequest;
import com.example.dongyucar.management.controller.dto.ManagementResponse;
import com.example.dongyucar.management.service.ManagementService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/management")
@RequiredArgsConstructor
public class ManagementController {

    private final ManagementService service;
    private final ContactEmailUtil emailService;

    @PostMapping("/contact")
    public ManagementResponse createByClientContact(@Valid @RequestBody ClientCreateRequest req) {
        return service.createByClient(req);
    }

    @GetMapping("/{id}")
    public ManagementResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public Page<ManagementResponse> list(Pageable pageable) {
        return service.list(pageable);
    }

    @PatchMapping("/{id}/admin")
    public ManagementResponse updateAdmin(@PathVariable Long id,
                                          @RequestBody AdminUpdateRequest req) {
        return service.updateAdmin(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/mail")
    public ResponseEntity<String> submitContactForm(@RequestBody ContactRequest request) {
        try {
            emailService.sendContactEmail(request);
            return ResponseEntity.ok("문의가 성공적으로 전송되었습니다.");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("문의 전송 중 오류가 발생했습니다.");
        }
    }
}
