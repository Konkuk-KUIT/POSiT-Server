package com.posit.posit.global.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "API 공통 에러 응답")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        @Schema(description = "성공 여부", example = "false")
        boolean isSuccess,

        @Schema(description = "에러 코드 정보")
        ErrorCode errorCode,

        @Schema(description = "필드 유효성 검증 실패 목록 (옵션)")
        List<FieldErrorDetail> errors
) {
    public static ErrorResponse fail(ErrorCode errorCode) {
        return new ErrorResponse(false, errorCode, null);
    }

    public static ErrorResponse fail(ErrorCode errorCode, List<FieldErrorDetail> errors) {
        return new ErrorResponse(false, errorCode, errors);
    }

    @JsonProperty("code")
    @Schema(description = "HTTP 상태 코드", example = "400")
    public int code() {
        return errorCode.getCode();
    }

    @Schema(description = "필드 에러 상세 정보")
    public record FieldErrorDetail(
            @Schema(description = "에러 발생 필드명", example = "email")
            String field,

            @Schema(description = "에러 메시지", example = "이메일 형식이 올바르지 않습니다.")
            String message
    ) {
        public static FieldErrorDetail of(String field, String message) {
            return new FieldErrorDetail(field, message);
        }
    }
}