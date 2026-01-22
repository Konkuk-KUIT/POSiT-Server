package com.posit.posit.domain.store.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "store_filter",
        uniqueConstraints = { @UniqueConstraint(name = "uq_store_filter", columnNames = {"store_id", "filter_id"}) },
        indexes = {
                @Index(name = "idx_store_filter_store", columnList = "store_id"),
                @Index(name = "idx_store_filter_filter", columnList = "filter_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class StoreFilter {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false, foreignKey = @ForeignKey(name = "fk_store_filter_store"))
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "filter_id", nullable = false, foreignKey = @ForeignKey(name = "fk_store_filter_filter"))
    private Filter filter;
}