package com.example.dongyucar.review.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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

    public void addImage(ReviewImage image) {
        this.images.add(image);
        image.setReview(this);
    }

    public void setReviewContent(ReviewContent content) {
        this.reviewContent = content;
        content.setReview(this);
    }
}
