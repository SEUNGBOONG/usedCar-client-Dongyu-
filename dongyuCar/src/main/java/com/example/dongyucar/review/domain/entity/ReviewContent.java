package com.example.dongyucar.review.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ReviewContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;
}
