package com.posit.posit.domain.concern.entity;

import com.posit.posit.domain.coupon.entity.CouponTemplate;
import com.posit.posit.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "concern",
        indexes = {
                @Index(name = "idx_concern_store", columnList = "store_id"),
                @Index(name = "idx_concern_template", columnList = "template_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Concern {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConcernStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false, foreignKey = @ForeignKey(name = "fk_concern_store"))
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id", nullable = false, foreignKey = @ForeignKey(name = "fk_concern_template"))
    private CouponTemplate template;


    public void update(String content, CouponTemplate template) {
        this.content = content;
        this.template = template;
    }
}
