package com.example.dongyucar.review.controller;

import com.example.dongyucar.review.dto.ReviewRequestDto;
import com.example.dongyucar.review.dto.ReviewResponseDto;
import com.example.dongyucar.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ReviewResponseDto createReview(@ModelAttribute ReviewRequestDto dto) throws Exception {
        // @ModelAttribute를 써야 MultipartFile과 String content가 동시에 맵핑됩니다.
        return reviewService.createReview(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }
}
