package com.example.dongyucar.review.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReviewContent reviewContent;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewImage> images = new ArrayList<>();

    // 연관관계 편의 메서드: 이미지 추가 시 이미지 객체에도 Review를 세팅
    public void addImage(ReviewImage image) {
        if (this.images == null) this.images = new ArrayList<>();
        this.images.add(image);
        image.setReview(this); // 중요: 자식에게 부모 주입
    }

    // 연관관계 편의 메서드: 컨텐츠 추가 시 컨텐츠 객체에도 Review를 세팅
    public void setReviewContent(ReviewContent content) {
        this.reviewContent = content;
        if (content != null) {
            content.setReview(this); // 중요: 자식에게 부모 주입
        }
    }
}
