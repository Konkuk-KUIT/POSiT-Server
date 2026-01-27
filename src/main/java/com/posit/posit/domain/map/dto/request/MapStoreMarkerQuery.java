package com.posit.posit.domain.map.dto.request;

import com.posit.posit.domain.store.entity.StoreFilter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MapStoreMarkerQuery(
    @NotNull
    @Min(-90) @Max(90)
    Double swLat,
    @NotNull
    @Min(-90) @Max(90)
    Double swLng,
    @NotNull
    @Min(-90) @Max(90)
    Double neLat,
    @NotNull
    @Min(-90) @Max(90)
    Double neLng,

    String keyword,
    String type,
    Integer limit
) {
}
