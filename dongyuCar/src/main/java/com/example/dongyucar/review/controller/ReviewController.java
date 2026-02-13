package com.example.dongyucar.review.controller;

import com.example.dongyucar.review.dto.ReviewResponseDto;
import com.example.dongyucar.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public Page<ReviewResponseDto> list(@RequestParam(defaultValue = "0") int page) {
        return reviewService.getReviewPage(page);
    }

    @GetMapping("/{id}")
    public ReviewResponseDto detail(@PathVariable Long id) {
        return reviewService.getReview(id);
    }
}
