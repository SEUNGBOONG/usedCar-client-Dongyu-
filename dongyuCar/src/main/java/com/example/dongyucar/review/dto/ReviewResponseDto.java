package com.example.dongyucar.review.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReviewResponseDto {
    private Long id;
    private String title;

    // 상세 페이지에서만 사용
    private String content;
    private List<String> imageUrls;

    // 목록 페이지에서만 사용
    private String thumbnail;
}
