package com.posit.posit.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "review_choice",
        uniqueConstraints = { @UniqueConstraint(name = "uq_review_choice", columnNames = {"review_id", "review_item_id"}) },
        indexes = {
                @Index(name = "idx_review_choice_review", columnList = "review_id"),
                @Index(name = "idx_review_choice_item", columnList = "review_item_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ReviewChoice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false, foreignKey = @ForeignKey(name = "fk_review_choice_review"))
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_review_choice_item"))
    private ReviewItem reviewItem;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}