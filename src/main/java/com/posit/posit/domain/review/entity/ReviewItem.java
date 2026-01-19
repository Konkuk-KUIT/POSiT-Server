package com.posit.posit.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "review_item",
        uniqueConstraints = { @UniqueConstraint(name = "uq_review_item_code", columnNames = "code") }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ReviewItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 20, nullable = false)
    private String code;

    @Column(name = "display_name", length = 255, nullable = false)
    private String displayName;
}
