package com.posit.posit.domain.map.dto.response;

import java.util.List;

public record MapStoreMarkerResponse(
        List<MarkerStore> stores
) {
    public record MarkerStore(
            long storeId,
            String name,
            double lat,
            double lng
    ) {}
}
