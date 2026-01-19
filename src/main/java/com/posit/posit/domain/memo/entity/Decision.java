package com.posit.posit.domain.memo.entity;

import com.posit.posit.domain.coupon.entity.CouponTemplate;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "decision",
        uniqueConstraints = { @UniqueConstraint(name = "uq_decision_memo", columnNames = "memo_id") },
        indexes = { @Index(name = "idx_decision_coupon_template", columnList = "coupon_template_id") }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Decision {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1:1 강제 (UNIQUE memo_id)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "memo_id", nullable = false, foreignKey = @ForeignKey(name = "fk_decision_memo"))
    private Memo memo;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private DecisionType type;

    @Column(name = "message", length = 50)
    private String message;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_template_id", foreignKey = @ForeignKey(name = "fk_decision_coupon_template"))
    private CouponTemplate couponTemplate;

    @Enumerated(EnumType.STRING)
    @Column(name = "reject_code")
    private RejectCode rejectCode;
}