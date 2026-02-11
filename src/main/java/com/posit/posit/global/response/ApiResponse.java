package com.posit.posit.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.domain.Slice;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean isSuccess,
        T data,
        Meta meta
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> success(T data, Meta meta) {
        return new ApiResponse<>(true, data, meta);
    }

    public static <T> ApiResponse<List<T>> success(Slice<T> slice) {
        return new ApiResponse<>(
                true,
                slice.getContent(), // data에는 알맹이(List)만 넣음
                Meta.from(slice)    // meta는 Slice 정보로 생성
        );
    }
}
