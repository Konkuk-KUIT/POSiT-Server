package com.posit.posit.domain.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "filter",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_filter_code", columnNames = "code"),
                @UniqueConstraint(name = "uq_filter_display_name", columnNames = "display_name")
        },
        indexes = { @Index(name = "idx_filter_category", columnList = "category") }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Filter {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", length = 20, nullable = false)
    private String category; // TYPE, MOOD

    @Enumerated(EnumType.STRING)
    @Column(name = "code", length = 10, nullable = false)
    private StoreFilterCategory code;

    @Column(name = "display_name", length = 20, nullable = false)
    private String displayName;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}