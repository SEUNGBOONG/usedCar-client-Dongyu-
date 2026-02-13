package com.example.dongyucar.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor  // ⭐️ 이거 필수! JSON 변환기가 빈 그릇을 만들 때 씁니다.
@AllArgsConstructor // ⭐️ 이거 필수! 빌더가 값을 채울 때 씁니다.
public class ReviewResponseDto {
    private Long id;
    private String title;

    // 상세 페이지용
    private String content;
    private List<String> imageUrls;

    // 목록 페이지용
    private String thumbnail;
}
