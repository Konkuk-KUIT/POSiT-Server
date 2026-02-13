package com.posit.posit.domain.store.dto.response;

public record ConcernCreateResponse(
        Long concernId,
        Long StoreId,
        Long templateId
) {
}
