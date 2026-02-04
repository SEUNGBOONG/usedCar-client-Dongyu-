package com.example.dongyucar.management.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class ManagementPageResponse {
    private List<ManagementResponse> data;
    private int page;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private boolean last;

    public static ManagementPageResponse of(Page<ManagementResponse> page) {
        return new ManagementPageResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isLast()
        );
    }
}
