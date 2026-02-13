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

    // cascade = CascadeType.ALL를 통해 Review 저장 시 ReviewContent도 자동 저장됩니다.
    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReviewContent reviewContent;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewImage> images = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addImage(ReviewImage image) {
        this.images.add(image);
        image.setReview(this);
    }

    public void setReviewContent(ReviewContent content) {
        this.reviewContent = content;
        if (content != null) {
            content.setReview(this); // 자식에게도 부모 주입 (필수)
        }
    }
}
