package com.posit.posit.domain.store.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "store_convince",
        uniqueConstraints = { @UniqueConstraint(name = "uq_store_convince", columnNames = {"store_id", "convince_id"}) },
        indexes = {
                @Index(name = "idx_store_convince_store", columnList = "store_id"),
                @Index(name = "idx_store_convince_convince", columnList = "convince_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class StoreConvince {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false, foreignKey = @ForeignKey(name = "fk_store_convince_store"))
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "convince_id", nullable = false, foreignKey = @ForeignKey(name = "fk_store_convince_convince"))
    private Convince convince;
}