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
        Review review = reviewRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        return convertToDetailResponse(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviewPage(int page) {
        Pageable pageable = PageRequest.of(page, 6, Sort.by("id").descending());
        // findAll 시 성능을 위해 reviewContent를 같이 가져오도록 조절할 수도 있지만,
        // 우선은 확실하게 데이터가 나오도록 기본 페이징 처리를 유지합니다.
        return reviewRepository.findAll(pageable).map(this::convertToListResponse);
    }

    // 상세 조회용 변환 (이미지 전체 리스트 포함)
    private ReviewResponseDto convertToDetailResponse(Review review) {
        String contentText = (review.getReviewContent() != null) ? review.getReviewContent().getContent() : "";
        List<String> imageUrls = review.getImages().stream()
                .map(ReviewImage::getImageUrl)
                .collect(Collectors.toList());

        System.out.println(">>> [DEBUG] 상세조회 ID: " + review.getId() + " | 내용: " + contentText);

        return ReviewResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(contentText)
                .imageUrls(imageUrls)
                .build();
    }

    // ⭐️ 리스트 조회용 변환 (프론트 요청에 따라 content 추가!)
    private ReviewResponseDto convertToListResponse(Review review) {
        // 리스트에서도 글 내용을 보여주기 위해 추가
        String contentText = (review.getReviewContent() != null) ? review.getReviewContent().getContent() : "";

        // 썸네일 (첫 번째 이미지)
        String thumbnail = (review.getImages() != null && !review.getImages().isEmpty())
                ? review.getImages().get(0).getImageUrl() : null;

        return ReviewResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(contentText) // 이제 리스트에서도 null이 안 나옵니다!
                .thumbnail(thumbnail)
                .build();
    }

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
