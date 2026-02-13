package com.example.dongyucar.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor  // ⭐️ 잭슨(JSON 변환기)이 객체를 만들 때 필수!
@AllArgsConstructor // ⭐️ 빌더 패턴과 함께 쓰일 때 필수!
public class ReviewResponseDto {
    private Long id;
    private String title;

    // 상세 페이지에서만 사용
    private String content;
    private List<String> imageUrls;

    // 목록 페이지에서만 사용
    private String thumbnail;
}
