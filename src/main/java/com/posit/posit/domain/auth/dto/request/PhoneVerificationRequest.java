package com.posit.posit.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PhoneVerificationRequest(
        @NotBlank
        @Size(min = 11, max = 11)
        String phone
) {
}
