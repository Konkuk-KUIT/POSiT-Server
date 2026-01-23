package com.posit.posit.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PhoneVerificationConfirmRequest (
        @NotNull
        Long verificationId,
        @NotBlank
        String phone,
        @NotBlank
        String code
){
}
