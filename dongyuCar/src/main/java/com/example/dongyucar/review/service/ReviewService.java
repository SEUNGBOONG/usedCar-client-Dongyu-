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
        // 1. 리뷰 기본 정보 생성
        Review review = Review.builder()
                .title(dto.getTitle())
                .build();

        // 2. 본문 연결
        if (dto.getContent() != null) {
            ReviewContent reviewContent = ReviewContent.builder()
                    .content(dto.getContent())
                    .build();
            review.setReviewContent(reviewContent);
        }

        // 3. 리뷰 먼저 저장하여 ID 확보
        Review savedReview = reviewRepository.save(review);

        // 4. 이미지 업로드 및 연관관계 매핑 (중요)
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            uploadReviewImages(dto.getImages(), savedReview);
        }

        // 5. 최종 변환 및 반환
        return convertToDetailResponse(savedReview);
    }

    private void uploadReviewImages(List<MultipartFile> files, Review review) {
        files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .forEach(file -> {
                    try {
                        String url = s3Service.uploadFile(
                                file.getInputStream(),
                                file.getOriginalFilename(),
                                file.getSize(),
                                file.getContentType()
                        );

                        // 연관관계 편의 메서드 호출로 Review 객체의 images 리스트에 즉시 반영
                        ReviewImage image = ReviewImage.builder()
                                .imageUrl(url)
                                .build();
                        review.addImage(image);

                    } catch (IOException e) {
                        throw new RuntimeException("이미지 업로드 실패", e);
                    }
                });

        // 변경 감지(Dirty Checking) 또는 명시적 저장
        reviewRepository.save(review);
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
        review.getImages().forEach(img -> s3Service.deleteFile(img.getImageUrl()));
        reviewRepository.delete(review);
    }

    private ReviewResponseDto convertToDetailResponse(Review review) {
        // 이미지 URL 리스트 추출
        List<String> imageUrls = review.getImages().stream()
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
