package com.example.dongyucar.review.controller;

import com.example.dongyucar.review.dto.ReviewRequestDto;
import com.example.dongyucar.review.dto.ReviewResponseDto;
import com.example.dongyucar.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType; // 추가
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    // consumes 추가: Swagger에서 파일 업로드 버튼 활성화
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ReviewResponseDto createReview(@ModelAttribute ReviewRequestDto dto) throws Exception {
        return reviewService.createReview(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }
}
