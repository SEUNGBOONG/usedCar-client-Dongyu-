package com.example.dongyucar.review.controller;

import com.example.dongyucar.review.dto.ReviewRequestDto;
import com.example.dongyucar.review.dto.ReviewResponseDto;
import com.example.dongyucar.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ReviewResponseDto createReview(@ModelAttribute ReviewRequestDto dto) throws Exception {
        return reviewService.createReview(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }
}
