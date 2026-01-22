package com.posit.posit.domain.auth.dto.response;

import com.posit.posit.domain.auth.entity.PhoneVerification;
import com.posit.posit.domain.auth.entity.PhoneVerificationStatus;

import java.time.LocalDateTime;

public record PhoneVerificationResponse(
        Long verificationId,
        LocalDateTime expiredAt,
        int resendCount,
        int attemptCount,
        PhoneVerificationStatus status
) {
    public static PhoneVerificationResponse from(PhoneVerification pv) {
        return new PhoneVerificationResponse(
                pv.getId(),
                pv.getExpiredAt(),
                pv.getResendCount(),
                pv.getAttemptCount(),
                pv.getStatus()
        );
    }
}
