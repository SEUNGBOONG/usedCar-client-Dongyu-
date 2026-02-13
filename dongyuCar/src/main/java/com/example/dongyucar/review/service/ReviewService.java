package com.example.dongyucar.review.service;

import com.example.dongyucar.review.domain.entity.Review;
import com.example.dongyucar.review.domain.entity.ReviewContent;
import com.example.dongyucar.review.domain.entity.ReviewImage;
import com.example.dongyucar.review.domain.repository.ReviewRepository;
import com.example.dongyucar.review.dto.ReviewRequestDto;
import com.example.dongyucar.review.dto.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 추가
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // 데이터 일관성을 위해 추가
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final S3Service s3Service;

    public ReviewResponseDto createReview(ReviewRequestDto dto) throws Exception {

        // 1. 리뷰 기본 정보 생성
        Review review = Review.builder()
                .title(dto.getTitle())
                .build();

        // 2. 리뷰 본문 연결
        ReviewContent content = ReviewContent.builder()
                .content(dto.getContent())
                .review(review)
                .build();
        review.setReviewContent(content);

        // 3. 리뷰 먼저 저장 (ID 발급)
        reviewRepository.save(review);

        // 4. 이미지 업로드 (비동기 제거, 동기 방식으로 변경)
        uploadReviewImages(dto.getImages(), review);

        // 5. 최종 결과 반환
        return convertToDetailResponse(review);
    }

    // 이미지 업로드 공통 로직
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

                        review.addImage(
                                ReviewImage.builder()
                                        .imageUrl(url)
                                        .review(review)
                                        .build()
                        );
                    } catch (IOException e) {
                        throw new RuntimeException("리뷰 이미지 업로드 실패", e);
                    }
                });

        // 이미지 정보가 업데이트된 review 객체는 트랜잭션 종료 시 자동 반영(Dirty Checking)되거나
        // 명시적으로 save를 한 번 더 호출할 수 있습니다.
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

        // S3 이미지 실물 삭제
        review.getImages().forEach(img -> s3Service.deleteFile(img.getImageUrl()));

        reviewRepository.delete(review);
    }

    private ReviewResponseDto convertToDetailResponse(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(review.getReviewContent() != null ? review.getReviewContent().getContent() : null)
                .imageUrls(
                        review.getImages().stream()
                                .map(ReviewImage::getImageUrl)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private ReviewResponseDto convertToListResponse(Review review) {
        String thumbnail = review.getImages().isEmpty()
                ? null
                : review.getImages().get(0).getImageUrl();

        return ReviewResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .thumbnail(thumbnail)
                .build();
    }
}
