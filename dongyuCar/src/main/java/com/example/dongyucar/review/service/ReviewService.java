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

    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long id) {
        // [수정] Fetch Join을 사용한 Repository 메서드 호출
        Review review = reviewRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        return convertToDetailResponse(review);
    }

    private ReviewResponseDto convertToDetailResponse(Review review) {
        // [핵심] DB 스크린샷에서 확인된 review_content 테이블의 데이터를 안전하게 추출
        String contentText = "";
        if (review.getReviewContent() != null) {
            contentText = review.getReviewContent().getContent();
        }

        List<String> imageUrls = review.getImages().stream()
                .map(ReviewImage::getImageUrl)
                .collect(Collectors.toList());

        // 로컬/서버 로그에서 데이터 유무를 반드시 확인하기 위한 출력
        System.out.println(">>> [DEBUG] 상세조회 실행 - ID: " + review.getId());
        System.out.println(">>> [DEBUG] 내용: " + contentText);

        return ReviewResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(contentText)
                .imageUrls(imageUrls)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviewPage(int page) {
        Pageable pageable = PageRequest.of(page, 6, Sort.by("id").descending());
        return reviewRepository.findAll(pageable).map(this::convertToListResponse);
    }

    private ReviewResponseDto convertToListResponse(Review review) {
        String thumbnail = (review.getImages() != null && !review.getImages().isEmpty())
                ? review.getImages().get(0).getImageUrl() : null;

        return ReviewResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .thumbnail(thumbnail)
                .build();
    }

    // 리뷰 생성 시 양방향 연관관계 설정이 아주 중요합니다.
    public ReviewResponseDto createReview(ReviewRequestDto dto) throws Exception {
        Review review = Review.builder().title(dto.getTitle()).build();

        if (dto.getContent() != null && !dto.getContent().isBlank()) {
            ReviewContent content = ReviewContent.builder().content(dto.getContent()).build();
            review.setReviewContent(content); // Review 클래스의 연관관계 편의 메서드 호출
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
