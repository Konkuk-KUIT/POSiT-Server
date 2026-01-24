package com.posit.posit.domain.auth.dto.request;

import jakarta.validation.constraints.NotNull;

public record LogoutRequest(
        @NotNull
        String refreshToken
) {
}
