package com.posit.posit.domain.store.dto.response;

import com.posit.posit.domain.concern.entity.Concern;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ConcernUpdateResponse {
    private Long concernId;
    private String concernContent;
    private Long templateId;
    private LocalDateTime updatedAt;

    public static ConcernUpdateResponse from(Concern concern) {
        return ConcernUpdateResponse.builder()
                .concernId(concern.getId())
                .concernContent(concern.getContent())
                .templateId(concern.getTemplate().getId())
                .updatedAt(concern.getUpdatedAt())
                .build();
    }
}