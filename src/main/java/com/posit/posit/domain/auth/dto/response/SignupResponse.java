package com.posit.posit.domain.auth.dto.response;

public record SignupResponse(
        Long userId,
        String role,
        TokenResponse tokens
) {
}
