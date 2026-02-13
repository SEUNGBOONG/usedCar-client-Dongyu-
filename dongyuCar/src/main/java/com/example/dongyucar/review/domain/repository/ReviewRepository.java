package com.example.dongyucar.review.domain.repository;

import com.example.dongyucar.review.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select distinct r from Review r " +
            "left join fetch r.reviewContent " +
            "left join fetch r.images " +
            "where r.id = :id")
    Optional<Review> findByIdWithDetails(@Param("id") Long id);
}
