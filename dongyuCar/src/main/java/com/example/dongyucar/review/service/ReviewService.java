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
        // 1. 리뷰 객체 생성
        Review review = Review.builder()
                .title(dto.getTitle())
                .build();

        // 2. 본문 연결 (Review 엔티티에 있는 편의 메서드 setReviewContent 활용)
        if (dto.getContent() != null && !dto.getContent().isEmpty()) {
            ReviewContent reviewContent = ReviewContent.builder()
                    .content(dto.getContent())
                    .build();
            review.setReviewContent(reviewContent);
        }

        // 3. 이미지 업로드 및 연결
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
                    review.addImage(image); // review 객체에 이미지 연결 (addImage 내부에서 setReview(this) 호출됨)
                }
            }
        }

        // 4. DB 저장 및 강제 동기화 (중요 포인트)
        Review savedReview = reviewRepository.save(review);
        reviewRepository.flush(); // 메모리에 있는 Insert 쿼리들을 즉시 DB로 보냄

        // 5. DB에서 다시 조회 (메모리 캐시 대신 DB에 저장된 완전한 데이터를 가져옴)
        Review finalReview = reviewRepository.findById(savedReview.getId())
                .orElseThrow(() -> new RuntimeException("리뷰 저장 후 조회 실패"));

        return convertToDetailResponse(finalReview);
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
