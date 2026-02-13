package com.example.dongyucar.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


// @Data 대신 직접 Getter를 써서 Jackson이 못 찾는 문제를 해결합니다.
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private String title;
    private String content;
    private List<String> imageUrls;
    private String thumbnail;

    // 수동 Getter 추가 (Jackson은 'get'으로 시작하는 메서드를 통해 데이터를 가져갑니다)
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getImageUrls() { return imageUrls; }
    public String getThumbnail() { return thumbnail; }
}
