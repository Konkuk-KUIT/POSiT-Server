package com.posit.posit.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OwnerProfileRequest(
        @NotBlank
        String businessNumber,
        @NotNull @Size(min = 4, max=4) // 정확히 4자리
        String couponPin
) {
}
