package com.posit.posit.domain.auth.dto.response;

import com.posit.posit.domain.user.entity.User;

public record SignupResponse(
        Long userId,
        String role,
        TokenResponse tokens
) {
    public static SignupResponse of(User user, String accessToken, String refreshToken) {
        return new SignupResponse(user.getId(), String.valueOf(user.getRole()), TokenResponse.of(accessToken, refreshToken));
    }

}
