package com.posit.posit.domain.map.dto.response;

import com.posit.posit.domain.store.entity.MenuType;
import com.posit.posit.domain.store.entity.StoreType;
import com.posit.posit.domain.store.entity.Weekday;

import java.util.List;

public record MapStoreDetailResponse(
        Long storeId,
        String name,
        StoreType category,
        String typeCode,
        String description,
        String statusCode,
        String openTime,
        Weekday notOpen,
        Address address,
        Location location,
        String snsLink,
        List<ConvinceItem> convince,
        List<ImageItem> images,
        List<MenuItem> menu,
        PositPreview positPreview
) {
    public record Address(String road, String lot) {}
    public record Location(Double lat, Double Lng) {}
    public record ConvinceItem(String displayName) {}
    public record ImageItem(
            Long imageId,
            String imageUrl,
            String thumbnailUrl,
            Integer order
    ) {}
    public record MenuItem(
            String imageUrl,
            MenuType type,
            String name,
            Integer price,
            Integer order
    ) {}

    public record PositPreview(
            ConcernPreview concern,
            List<MemoPreview> memos
    ) {
        public record ConcernPreview(Long concernId, String content) {}
        public record MemoPreview(Long memoId, String content) {}
    }
}
