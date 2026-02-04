package com.example.dongyucar.review.domain.repository;


import com.example.dongyucar.review.domain.entity.ReviewContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewContentRepository extends JpaRepository<ReviewContent, Long> {
}
