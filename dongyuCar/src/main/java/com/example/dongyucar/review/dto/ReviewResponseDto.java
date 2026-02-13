package com.example.dongyucar.review.dto;

import lombok.*;
import java.util.List;

@Getter // ⭐️ Jackson이 데이터를 읽어갈 때 필수
@Setter // ⭐️ 데이터를 담을 때 필요
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private String title;
    private String content;
    private List<String> imageUrls;
    private String thumbnail;
}
