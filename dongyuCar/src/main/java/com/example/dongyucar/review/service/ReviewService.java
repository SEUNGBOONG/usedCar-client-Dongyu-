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

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final S3Service s3Service;

    public ReviewResponseDto createReview(ReviewRequestDto dto) throws Exception {
        Review review = Review.builder().title(dto.getTitle()).build();

        if (dto.getContent() != null && !dto.getContent().isEmpty()) {
            review.setReviewContent(ReviewContent.builder().content(dto.getContent()).build());
        }

        if (dto.getImages() != null) {
            for (MultipartFile file : dto.getImages()) {
                if (!file.isEmpty()) {
                    String url = s3Service.uploadFile(file.getInputStream(), file.getOriginalFilename(), file.getSize(), file.getContentType());
                    review.addImage(ReviewImage.builder().imageUrl(url).build());
                }
            }
        }

        Review savedReview = reviewRepository.save(review);
        reviewRepository.flush();

        return getReview(savedReview.getId());
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long id) {
        Review review = reviewRepository.findByIdWithDetails(id)
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
        if (review.getImages() != null) {
            review.getImages().forEach(img -> s3Service.deleteFile(img.getImageUrl()));
        }
        reviewRepository.delete(review);
    }

    private ReviewResponseDto convertToDetailResponse(Review review) {
        // 엔티티에서 직접 String과 List<String>을 추출하여 DTO에 담습니다. (순환 참조 원천 차단)
        String contentText = (review.getReviewContent() != null) ? review.getReviewContent().getContent() : "";
        List<String> imageUrls = review.getImages().stream().map(ReviewImage::getImageUrl).collect(Collectors.toList());

        return ReviewResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(contentText)
                .imageUrls(imageUrls)
                .build();
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
}
