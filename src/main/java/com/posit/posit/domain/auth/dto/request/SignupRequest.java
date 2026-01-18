package com.posit.posit.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotNull
        String role,
        @NotBlank @Size(min = 4, max = 15)
        String loginId,
        @NotBlank @Size(min = 8, max = 15)
        String password,
        @NotBlank @Size(max = 10)
        String name,
        @NotBlank @Size(max = 11)
        String phone,
        OwnerProfileRequest ownerProfile,
        @NotBlank
        String signupToken
) {
}
