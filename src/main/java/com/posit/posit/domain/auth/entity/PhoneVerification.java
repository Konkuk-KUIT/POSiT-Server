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

    @Column(name = "signup_token_hash", length = 200)
    private String signupTokenHash;

    @Builder.Default
    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount = 0;

    @Builder.Default
    @Column(name = "resend_count", nullable = false)
    private Integer resendCount = 0;

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
        this.attemptCount = (this.attemptCount == null) ? 1 : this.getAttemptCount() + 1;
    }

    public void increaseResend() {
        this.resendCount = (this.resendCount == null) ? 1 : this.resendCount + 1;
    }

    public void issueSignupToken(String signupTokenHash) {
        this.signupTokenHash = signupTokenHash;
    }

    public void consumeSignupToken() {
        this.signupTokenHash = null;
    }

    @PrePersist
    private void prePersist() {
        if (attemptCount == null) attemptCount = 0;
        if (resendCount == null) resendCount = 0;
        if (status == null) status = PhoneVerificationStatus.PENDING;
    }
}