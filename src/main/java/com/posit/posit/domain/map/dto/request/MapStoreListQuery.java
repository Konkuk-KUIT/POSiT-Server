package com.posit.posit.domain.map.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MapStoreListQuery(
        @NotNull @Min(-90) @Max(90) Double swLat,
        @NotNull @Min(-180) @Max(180) Double swLng,
        @NotNull @Min(-90) @Max(90) Double neLat,
        @NotNull @Min(-180) @Max(180) Double neLng,

        @NotNull @Min(-90) @Max(90) Double myLat,
        @NotNull @Min(-180) @Max(180) Double myLng,

        String keyword,
        String type,

        // cursor는 문자열 토큰 (Base64 등)
        String cursor,

        @Min(1) @Max(50)
        Integer limit
) {
}
