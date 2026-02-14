package com.posit.posit.domain.store.dto.response;

import java.time.LocalDateTime;

public record AdoptionResultResponse(
        String concernTitle,
        String writer,
        String adoptedAt,
        String reward
) {
    public static AdoptionResultResponse of(String concernTitle, String writer, String adoptedAt, String reward) {
        return new AdoptionResultResponse(concernTitle, writer, adoptedAt, reward);
    }
}
