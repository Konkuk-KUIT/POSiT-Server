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
    // 1. 일반 성공 (데이터 있음)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    // 2. 일반 성공 (데이터 + 메타 직접 넣기)
    public static <T> ApiResponse<T> success(T data, Meta meta) {
        return new ApiResponse<>(true, data, meta);
    }

    // 3. 데이터 없는 성공 (로그인, 로그아웃 등) -> AuthController 해결!
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, null, null);
    }

    // 4. Slice 처리 (수신함, 쿠폰함 페이징) -> Owner/CouponController 해결!
    public static <T> ApiResponse<List<T>> success(Slice<T> slice) {
        return new ApiResponse<>(
                true,
                slice.getContent(), // List는 data로
                Meta.from(slice)    // 페이징 정보는 meta로
        );
    }

    public static <T> ApiResponse<T> success(T data, Long nextCursorId) {
        return new ApiResponse<>(true, data, Meta.from(nextCursorId));
    }
}