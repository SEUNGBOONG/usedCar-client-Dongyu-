package com.example.dongyucar.review.domain.entity;

import jakarta.persistence.*;
import lombok.*;
@Getter @Setter
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
    private Review review; // ⭐️ DTO 변환 시 이 review 객체를 건드리지 않게 주의!
}
