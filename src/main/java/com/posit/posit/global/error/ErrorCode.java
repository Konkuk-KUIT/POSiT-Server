package com.posit.posit.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // -------------------------------------------------
    // 5xx
    // -------------------------------------------------
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50000, "서버 내부 오류가 발생했습니다."),

    // -------------------------------------------------
    // Common 4xx
    // -------------------------------------------------
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 40000, "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 40100, "인증에 실패했습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, 40300, "권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, 40400, "리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 40500, "허용되지 않은 Http 메서드입니다."),

    // -------------------------------------------------
    // Auth / JWT
    // -------------------------------------------------
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, 40101, "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 40102, "유효하지 않은 RefreshToken"),

    // -------------------------------------------------
    // Phone verification
    // -------------------------------------------------
    PHONE_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, 40103, "번호 인증이 완료되지 않았습니다."),
    PHONE_VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, 40001, "인증 코드가 틀렸습니다."),
    PHONE_VERIFICATION_EXPIRED(HttpStatus.BAD_REQUEST, 40002, "만료된 인증번호입니다. 다시 요청하십쇼."),
    PHONE_VERIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, 40401, "번호가 맞지 않습니다."),
    PHONE_VERIFICATION_ATTEMPT_LIMIT(HttpStatus.FORBIDDEN, 40301, "휴대폰 인증 횟수 초과 제한"),

    // -------------------------------------------------
    // Conflict / duplicate
    // -------------------------------------------------
    DUPLICATE_PHONE(HttpStatus.CONFLICT, 40901, "이미 계정이 존재하는 번호입니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, 40902, "이미 존재하는 loginId입니다."),

    // -------------------------------------------------
    // Not found (domain)
    // -------------------------------------------------
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, 40402, "해당 가게를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 40403, "해당 유저를 찾을 수 없습니다"),

    // -------------------------------------------------
    // Other validation
    // -------------------------------------------------
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, 40003, "유효하지 않는 비밀번호입니다."),
    INVALID_PIN(HttpStatus.BAD_REQUEST, 40004, "PIN 비밀번호가 일치하지 않습니다.");
    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
