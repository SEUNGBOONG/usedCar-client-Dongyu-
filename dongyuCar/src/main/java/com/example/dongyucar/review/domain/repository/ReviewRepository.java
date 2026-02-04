package com.example.dongyucar.review.domain.repository;

import com.example.dongyucar.review.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
