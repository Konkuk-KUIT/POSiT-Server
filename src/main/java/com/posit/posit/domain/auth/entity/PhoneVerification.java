package com.posit.posit.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "phone_verification",
        indexes = {
                @Index(name = "idx_phone_created", columnList = "phone, created_at"),
                @Index(name = "idx_phone_expired", columnList = "phone, expired_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PhoneVerification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone", length = 11, nullable = false)
    private String phone;

    @Column(name = "code_hash", length = 200, nullable = false)
    private String codeHash;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount;

    @Column(name = "resend_count", nullable = false)
    private Integer resendCount;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PhoneVerificationStatus status;

    public boolean isVerified() {
        return status == PhoneVerificationStatus.VERIFIED
                && verifiedAt != null;
    }

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expiredAt) || status == PhoneVerificationStatus.EXPIRED;
    }

    public void updateCodeHash(String codeHash) {
        this.codeHash = codeHash;
    }

    public void updateExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public void markPending() {
        this.status = PhoneVerificationStatus.PENDING;
        this.verifiedAt = null;
    }

    public void markVerified(LocalDateTime now) {
        this.status = PhoneVerificationStatus.VERIFIED;
        this.verifiedAt = now;
    }

    public void increaseAttempt() {
        this.attemptCount++;
    }

    public void increaseResend() {
        this.resendCount++;
    }
}