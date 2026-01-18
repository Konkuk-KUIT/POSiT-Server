package com.posit.posit.domain.auth.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
