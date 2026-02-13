package com.example.dongyucar.review.domain.repository;

import com.example.dongyucar.review.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 리뷰 상세를 가져올 때 본문(content)과 이미지(images)를 한 번에 다 가져오도록 강제함
    @Query("select r from Review r " +
            "left join fetch r.reviewContent " +
            "left join fetch r.images " +
            "where r.id = :id")
    Optional<Review> findByIdWithDetails(@Param("id") Long id);
}
