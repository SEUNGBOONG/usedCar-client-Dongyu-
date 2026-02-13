package com.example.dongyucar.review.service;

import com.example.dongyucar.review.domain.entity.*;
import com.example.dongyucar.review.domain.repository.ReviewRepository;
import com.example.dongyucar.review.dto.ReviewRequestDto;
import com.example.dongyucar.review.dto.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final S3Service s3Service;

    public ReviewResponseDto createReview(ReviewRequestDto dto) throws Exception {
        // 1. 리뷰 객체 먼저 생성
        Review review = Review.builder()
                .title(dto.getTitle())
                .build();

        // 2. 본문 연관관계 매핑 (수정된 편의 메서드 사용)
        if (dto.getContent() != null && !dto.getContent().isEmpty()) {
            ReviewContent reviewContent = ReviewContent.builder()
                    .content(dto.getContent())
                    .build();
            review.setReviewContent(reviewContent);
        }

        // 3. 이미지 업로드 및 연관관계 매핑 (저장 전 리스트에 담기)
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (MultipartFile file : dto.getImages()) {
                if (file != null && !file.isEmpty()) {
                    String url = s3Service.uploadFile(
                            file.getInputStream(),
                            file.getOriginalFilename(),
                            file.getSize(),
                            file.getContentType()
                    );
                    ReviewImage image = ReviewImage.builder().imageUrl(url).build();
                    review.addImage(image); // review 객체에 이미지 연결
                }
            }
        }

        // 4. 최종 저장 (CascadeType.ALL로 인해 Image, Content 자동 저장)
        Review savedReview = reviewRepository.save(review);

        // 5. 즉시 반영 후 상세 데이터로 변환하여 반환
        return convertToDetailResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        return convertToDetailResponse(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviewPage(int page) {
        Pageable pageable = PageRequest.of(page, 6, Sort.by("id").descending());
        return reviewRepository.findAll(pageable).map(this::convertToListResponse);
    }

    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰 없음"));

        // S3 실물 파일 삭제
        if (review.getImages() != null) {
            review.getImages().forEach(img -> s3Service.deleteFile(img.getImageUrl()));
        }

        reviewRepository.delete(review);
    }

    private ReviewResponseDto convertToDetailResponse(Review review) {
        List<String> imageUrls = (review.getImages() == null) ? List.of() :
                review.getImages().stream()
                        .map(ReviewImage::getImageUrl)
                        .collect(Collectors.toList());

        return ReviewResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(review.getReviewContent() != null ? review.getReviewContent().getContent() : null)
                .imageUrls(imageUrls)
                .build();
    }

    private ReviewResponseDto convertToListResponse(Review review) {
        String thumbnail = (review.getImages() != null && !review.getImages().isEmpty())
                ? review.getImages().get(0).getImageUrl()
                : null;

        return ReviewResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .thumbnail(thumbnail)
                .build();
    }
}
