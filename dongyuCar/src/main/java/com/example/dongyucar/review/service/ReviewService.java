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
        // 1. 리뷰 객체 생성
        Review review = Review.builder()
                .title(dto.getTitle())
                .build();

        // 2. 본문 연결 (양방향 연결)
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
                    review.addImage(image);
                }
            }
        }

        // 4. 저장 및 동기화
        Review savedReview = reviewRepository.save(review);
        reviewRepository.flush();

        // 5. ⭐️ 핵심: 방금 만든 쿼리 메서드(findByIdWithDetails)로 다시 조회!
        // 이렇게 해야 null 없이 본문과 이미지가 꽉 찬 상태로 가져와집니다.
        Review finalReview = reviewRepository.findByIdWithDetails(savedReview.getId())
                .orElseThrow(() -> new RuntimeException("저장된 리뷰를 찾을 수 없습니다."));

        return convertToDetailResponse(finalReview);
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long id) {
        // ⭐️ 여기서도 findByIdWithDetails를 사용합니다.
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
        // ⭐️ 디버깅을 위해 로그를 찍어봅니다. (서버 콘솔에서 확인 가능)
        System.out.println("조회된 리뷰 ID: " + review.getId());
        System.out.println("본문 객체 존재 여부: " + (review.getReviewContent() != null));

        List<String> imageUrls = (review.getImages() == null) ? List.of() :
                review.getImages().stream()
                        .map(ReviewImage::getImageUrl)
                        .collect(Collectors.toList());

        System.out.println("이미지 개수: " + imageUrls.size());

        return ReviewResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                // 여기서 review.getReviewContent().getContent()를 직접 호출할 때
                // DB에서 값이 안 넘어오면 null이 됩니다.
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
