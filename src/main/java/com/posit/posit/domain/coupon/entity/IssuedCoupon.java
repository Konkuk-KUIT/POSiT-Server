package com.posit.posit.domain.coupon.entity;

import com.posit.posit.domain.memo.entity.Memo;
import com.posit.posit.domain.store.entity.Store;
import com.posit.posit.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "issued_coupon",
        indexes = {
                @Index(name = "idx_issued_coupon_user", columnList = "user_id"),
                @Index(name = "idx_issued_coupon_store", columnList = "store_id"),
                @Index(name = "idx_issued_coupon_status_exp", columnList = "status, expired_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class IssuedCoupon {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false, foreignKey = @ForeignKey(name = "fk_issued_coupon_store"))
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "memo_id", nullable = false, foreignKey = @ForeignKey(name = "fk_issued_coupon_memo"))
    private Memo memo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id", nullable = false, foreignKey = @ForeignKey(name = "fk_issued_coupon_template"))
    private CouponTemplate template;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_issued_coupon_user"))
    private User user;

    @Column(name = "title", length = 20, nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private String image;

    @Column(name = "`condition`", length = 30, nullable = false)
    private String condition;

    @Column(name = "issued_at", insertable = false, updatable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private IssuedCouponStatus status;

    // 쿠폰 사용 처리 메서드
    public void use() {
        // 1. 이미 사용한 경우
        if (this.status == IssuedCouponStatus.USED) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }

        // 2. 만료된 경우 (현재 시간이 만료일보다 뒤라면)
        if (LocalDateTime.now().isAfter(this.expiredAt)) { // 혹은 status == EXPIRED
            throw new IllegalStateException("유효기간이 만료된 쿠폰입니다.");
        }

        // 3. 상태 변경
        this.status = IssuedCouponStatus.USED;
        this.usedAt = LocalDateTime.now();
    }
}
