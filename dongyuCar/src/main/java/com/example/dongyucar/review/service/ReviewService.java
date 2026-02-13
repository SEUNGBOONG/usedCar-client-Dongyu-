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

        // 2. 리뷰 본문 연결 (dto.getContent() 값을 ReviewContent에 주입)
        if (dto.getContent() != null) {
            ReviewContent reviewContent = ReviewContent.builder()
                    .content(dto.getContent())
                    .build();
            review.setReviewContent(reviewContent); // 엔티티 내 편의메서드 호출
        }

        // 3. 리뷰 저장 (CascadeType.ALL 설정으로 Content도 자동 저장됨)
        Review savedReview = reviewRepository.save(review);

        // 4. 이미지 업로드
        uploadReviewImages(dto.getImages(), savedReview);

        return convertToDetailResponse(savedReview);
    }

    private void uploadReviewImages(List<MultipartFile> files, Review review) {
        if (files == null || files.isEmpty()) return;

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
                        review.addImage(ReviewImage.builder().imageUrl(url).build());
                    } catch (IOException e) {
                        throw new RuntimeException("리뷰 이미지 업로드 실패", e);
                    }
                });
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
        return ReviewResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(review.getReviewContent() != null ? review.getReviewContent().getContent() : null)
                .imageUrls(review.getImages().stream().map(ReviewImage::getImageUrl).collect(Collectors.toList()))
                .build();
    }

    private ReviewResponseDto convertToListResponse(Review review) {
        String thumbnail = review.getImages().isEmpty() ? null : review.getImages().get(0).getImageUrl();
        return ReviewResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .thumbnail(thumbnail)
                .build();
    }
}
