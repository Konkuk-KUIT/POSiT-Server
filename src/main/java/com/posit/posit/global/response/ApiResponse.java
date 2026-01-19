package com.posit.posit.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posit.posit.global.error.ErrorCode;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean isSuccess,
        T data,
        Meta meta,
        ErrorCode errorCode,
        List<FieldErrorDetail> errors
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null, null);
    }

    public static <T> ApiResponse<T> success(T data, Meta meta) {
        return new ApiResponse<>(true, data, meta, null, null);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<>(false, null, null, errorCode, null);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode, List<FieldErrorDetail> errors) {
        return new ApiResponse<>(false, null, null, errorCode, errors);
    }

    public record FieldErrorDetail(String field, String message) {
        public static FieldErrorDetail of(String field, String message) {
            return new FieldErrorDetail(field, message);
        }
    }
}
