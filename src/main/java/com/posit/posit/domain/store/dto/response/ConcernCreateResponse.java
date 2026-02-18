package com.posit.posit.domain.store.dto.response;

public record ConcernCreateResponse(
        Long concernId,
        Long StoreId,
        Long templateId
) {
    public static ConcernCreateResponse of(Long concernId, Long storeId, Long templateId) {
        return new ConcernCreateResponse(concernId, storeId, templateId);
    }
}
