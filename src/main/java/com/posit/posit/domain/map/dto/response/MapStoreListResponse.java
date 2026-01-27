package com.posit.posit.domain.map.dto.response;

import java.util.List;

public record MapStoreListResponse(
    List<StoreItem> stores
) {
    public record StoreItem(
            long storeId,
            String name,
            String address,
            double lat,
            double lng,
            String description,
            String statusCode,
            List<ImageItem> images
    ) {}

    public record ImageItem(
            long imageId,
            String thumbnailUrl,
            int order
    ) {}
}
