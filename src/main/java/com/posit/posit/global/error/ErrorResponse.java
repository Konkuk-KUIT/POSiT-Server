package com.posit.posit.global.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        boolean isSuccess,
        ErrorCode errorCode,
        List<FieldErrorDetail> errors
) {
    public static ErrorResponse fail(ErrorCode errorCode) {
        return new ErrorResponse(false, errorCode, null);
    }

    public static ErrorResponse fail(ErrorCode errorCode, List<FieldErrorDetail> errors) {
        return new ErrorResponse(false, errorCode, errors);
    }

    @JsonProperty("code")
    public int code() {
        return errorCode.getCode();
    }

    public record FieldErrorDetail(String field, String message) {
        public static FieldErrorDetail of(String field, String message) {
            return new FieldErrorDetail(field, message);
        }
    }
}
