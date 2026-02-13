package com.example.dongyucar.review.service;

import com.example.dongyucar.review.domain.entity.*;
import com.example.dongyucar.review.domain.repository.ReviewRepository;
import com.example.dongyucar.review.dto.ReviewRequestDto;
import com.example.dongyucar.review.dto.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final S3Service s3Service;

    // 상세 조회 (Swagger에서 null 나오는 문제 해결 지점)
    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long id) {
        Review review = reviewRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        // ⭐️ 직접 변환하여 데이터 누락 방지
        String contentText = (review.getReviewContent() != null) ? review.getReviewContent().getContent() : "";
        List<String> imageUrls = review.getImages().stream()
                .map(ReviewImage::getImageUrl)
                .collect(Collectors.toList());

        return ReviewResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(contentText)
                .imageUrls(imageUrls)
                .build();
    }

    // 목록 조회 (리뷰 리스트)
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviewPage(int page) {
        Pageable pageable = PageRequest.of(page, 6, Sort.by("id").descending());
        return reviewRepository.findAll(pageable).map(review ->
                ReviewResponseDto.builder()
                        .id(review.getId())
                        .title(review.getTitle())
                        .thumbnail(review.getImages().isEmpty() ? null : review.getImages().get(0).getImageUrl())
                        .build()
        );
    }

    // 생성
    public ReviewResponseDto createReview(ReviewRequestDto dto) throws Exception {
        Review review = Review.builder().title(dto.getTitle()).build();
        if (dto.getContent() != null) {
            review.setReviewContent(ReviewContent.builder().content(dto.getContent()).build());
        }
        if (dto.getImages() != null) {
            for (var file : dto.getImages()) {
                if (!file.isEmpty()) {
                    String url = s3Service.uploadFile(file.getInputStream(), file.getOriginalFilename(), file.getSize(), file.getContentType());
                    review.addImage(ReviewImage.builder().imageUrl(url).build());
                }
            }
        }
        Review saved = reviewRepository.save(review);
        return getReview(saved.getId());
    }

    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id).orElseThrow();
        review.getImages().forEach(img -> s3Service.deleteFile(img.getImageUrl()));
        reviewRepository.delete(review);
    }
}
