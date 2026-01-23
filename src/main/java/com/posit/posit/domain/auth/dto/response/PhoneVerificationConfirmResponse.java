package com.posit.posit.domain.auth.dto.response;

import com.posit.posit.domain.auth.dto.request.PhoneVerificationConfirmRequest;
import com.posit.posit.domain.auth.entity.PhoneVerification;
import com.posit.posit.domain.auth.entity.PhoneVerificationStatus;

import java.time.LocalDateTime;

public record PhoneVerificationConfirmResponse (
        boolean verified,
        PhoneVerificationStatus status,
        LocalDateTime verifiedAt,
        boolean isExistingUser,
        String signupToken,
        Long userId
){
    public static PhoneVerificationConfirmResponse from(PhoneVerification pv) {
        return new PhoneVerificationConfirmResponse(
                true,
                pv.getStatus(),
                pv.getVerifiedAt(),
                false,
                null,
                null
        );
    }

    public static PhoneVerificationConfirmResponse newUser(
            PhoneVerification pv,
            String signupToken
    ) {
        return new PhoneVerificationConfirmResponse(
                true,
                pv.getStatus(),
                pv.getVerifiedAt(),
                false,
                signupToken,
                null      // userId 없음
        );
    }
    public static PhoneVerificationConfirmResponse existing(
            PhoneVerification pv,
            Long userId
    ) {
        return new PhoneVerificationConfirmResponse(
                true,
                pv.getStatus(),
                pv.getVerifiedAt(),
                true,
                null,     // signupToken 없음
                userId
        );
    }
}