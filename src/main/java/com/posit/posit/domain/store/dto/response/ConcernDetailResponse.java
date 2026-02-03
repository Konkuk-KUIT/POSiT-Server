package com.posit.posit.domain.store.dto.response;

import com.posit.posit.domain.concern.entity.Concern;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ConcernDetailResponse {

    private Long concernId;
    private Long storeId;
    private String concernContent;
    private String concernStatus; // "OPEN" or "CLOSED"
    private Long templateId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ConcernDetailResponse from(Concern concern) {
        return ConcernDetailResponse.builder()
                .concernId(concern.getId())
                .storeId(concern.getStore().getId())
                .concernContent(concern.getContent())
                // 엔티티에 status 필드가 없다면 기본값 "OPEN" 또는 로직 적용
                .concernStatus("OPEN")
                .templateId(concern.getTemplate().getId())
                .createdAt(concern.getCreatedAt())
                .updatedAt(concern.getUpdatedAt()) // BaseEntity에 있다면 사용, 없다면 createdAt
                .build();
    }
}