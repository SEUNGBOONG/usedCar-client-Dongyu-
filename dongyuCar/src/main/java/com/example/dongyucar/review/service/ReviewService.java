package com.example.dongyucar.review.service;

import com.example.dongyucar.review.domain.entity.*;
import com.example.dongyucar.review.domain.repository.ReviewRepository;
import com.example.dongyucar.review.dto.ReviewRequestDto;
import com.example.dongyucar.review.dto.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final S3Service s3Service;

    public ReviewResponseDto createReview(ReviewRequestDto dto) throws Exception {
        Review review = Review.builder().title(dto.getTitle()).build();

        if (dto.getContent() != null && !dto.getContent().isEmpty()) {
            ReviewContent content = ReviewContent.builder().content(dto.getContent()).build();
            review.setReviewContent(content);
        }

        if (dto.getImages() != null) {
            for (MultipartFile file : dto.getImages()) {
                if (!file.isEmpty()) {
                    String url = s3Service.uploadFile(file.getInputStream(), file.getOriginalFilename(), file.getSize(), file.getContentType());
                    review.addImage(ReviewImage.builder().imageUrl(url).build());
                }
            }
        }

        Review saved = reviewRepository.save(review);
        reviewRepository.flush(); // DB 반영 강제

        return getReview(saved.getId());
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long id) {
        Review review = reviewRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("리뷰 없음: " + id));

        return convertToDetailResponse(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviewPage(int page) {
        Pageable pageable = PageRequest.of(page, 6, Sort.by("id").descending());
        return reviewRepository.findAll(pageable).map(this::convertToListResponse);
    }

    private ReviewResponseDto convertToDetailResponse(Review review) {
        String contentText = (review.getReviewContent() != null) ? review.getReviewContent().getContent() : "내용 없음";
        List<String> urls = review.getImages().stream()
                .map(ReviewImage::getImageUrl)
                .collect(Collectors.toList());

        // 서버 로그에서 확인용 (docker logs에 찍힙니다)
        System.out.println("== [상세조회 변환 시작] ID: " + review.getId() + " ==");
        System.out.println("추출된 내용: " + contentText);
        System.out.println("추출된 이미지 개수: " + urls.size());

        return ReviewResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(contentText)
                .imageUrls(urls)
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

    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id).orElseThrow();
        if (review.getImages() != null) {
            review.getImages().forEach(img -> s3Service.deleteFile(img.getImageUrl()));
        }
        reviewRepository.delete(review);
    }
}
