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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final S3Service s3Service;

    // 후기 생성
    public ReviewResponseDto createReview(ReviewRequestDto dto) throws Exception {

        Review review = Review.builder()
                .title(dto.getTitle())
                .build();

        ReviewContent content = ReviewContent.builder()
                .content(dto.getContent())
                .review(review)
                .build();

        review.setReviewContent(content);
        reviewRepository.save(review);

        // 🌟 1) 사용자에게 즉시 응답
        ReviewResponseDto response = convertToDetailResponse(review);

        // 🌟 2) 이미지 업로드는 백그라운드에서 실행
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {

            CompletableFuture.runAsync(() -> {
                dto.getImages().forEach(file -> {
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

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                reviewRepository.save(review);
            });
        }

        return response;
    }

    // 상세 조회
    public ReviewResponseDto getReview(Long id) {
        Review review = reviewRepository.findById(id).orElseThrow();
        return convertToDetailResponse(review);
    }


    // 페이징 목록 조회
    public Page<ReviewResponseDto> getReviewPage(int page) {
        Pageable pageable = PageRequest.of(page, 6, Sort.by("id").descending());
        return reviewRepository.findAll(pageable).map(this::convertToListResponse);
    }


    // 삭제 — S3 이미지까지 삭제 추가됨
    public void deleteReview(Long id) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰 없음"));

        // S3 이미지 삭제
        review.getImages().forEach(img -> s3Service.deleteFile(img.getImageUrl()));

        reviewRepository.delete(review);
    }


    // 상세 변환
    private ReviewResponseDto convertToDetailResponse(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(review.getReviewContent().getContent())
                .imageUrls(
                        review.getImages()
                                .stream()
                                .map(ReviewImage::getImageUrl)
                                .collect(Collectors.toList())
                )
                .build();
    }


    // 목록 변환
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
