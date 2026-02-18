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
    // 405 METHOD NOT ALLOWED
    // -------------------------------------------------
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 40500, "허용되지 않은 Http 메서드입니다."),

    // -------------------------------------------------
    // 404 - NOT FOUND
    // -------------------------------------------------
    NOT_FOUND(HttpStatus.NOT_FOUND, 40400, "리소스를 찾을 수 없습니다."),
    PHONE_VERIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, 40401, "번호가 맞지 않습니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, 40402, "해당 가게를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 40403, "해당 유저를 찾을 수 없습니다"),
    MEMO_NOT_FOUND(HttpStatus.NOT_FOUND, 40404, "해당 메모를 찾을 수 없습니다"),
    TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, 40405, "존재하지 않는 쿠폰 템플릿입니다"),
    FILTER_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, 40406, "지원하지 않는 필터 타입입니다."),
    CONVINCE_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, 40407, "지원하지 않는 편의시설 코드입니다."),
    CONCERN_NOT_FOUND(HttpStatus.NOT_FOUND, 40408, "해당 고민글을 찾을 수 없습니다."),

    // -------------------------------------------------
    // 403 FORBIDDEN
    // -------------------------------------------------
    FORBIDDEN(HttpStatus.FORBIDDEN, 40300, "권한이 없습니다."),
    MEMO_STORE_FORBIDDEN(HttpStatus.FORBIDDEN, 40301, "해당 매장의 메모가 아닙니다."),
    COUPON_TEMPLATE_FORBIDDEN(HttpStatus.FORBIDDEN, 40302, "본인의 쿠폰 템플릿만 사용 가능합니다"),

    // -------------------------------------------------
    // 401 UNAUTHORIZED
    // -------------------------------------------------
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 40100, "인증에 실패했습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, 40101, "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 40102, "유효하지 않은 RefreshToken"),
    PHONE_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, 40103, "번호 인증이 완료되지 않았습니다."),

    // -------------------------------------------------
    // 409 CONFLICT
    // -------------------------------------------------
    DUPLICATE_PHONE(HttpStatus.CONFLICT, 40901, "이미 계정이 존재하는 번호입니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, 40902, "이미 존재하는 loginId입니다."),
    MEMO_DECISION_DUPLICATE(HttpStatus.CONFLICT, 40903, "이미 처리가 완료된 메모입니다."),
    // -------------------------------------------------
    // 400 Bad Request
    // -------------------------------------------------
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 40000, "잘못된 요청입니다."),
    DTO_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, 40001, "요청값 (요청 본문)이 유효하지 않습니다"),
    PHONE_VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, 40003, "인증 코드가 틀렸습니다."),
    PHONE_VERIFICATION_EXPIRED(HttpStatus.BAD_REQUEST, 40004, "만료된 인증번호입니다. 다시 요청하십쇼."),
    PHONE_VERIFICATION_ATTEMPT_LIMIT(HttpStatus.BAD_REQUEST, 40005, "휴대폰 인증 횟수 초과 제한"),
    PHONE_VERIFICATION_RESEND_LIMIT(HttpStatus.BAD_REQUEST, 40006, "인증번호 재발송 횟수 초과 제한 (최대 3회)"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, 40007, "유효하지 않는 비밀번호입니다."),
    INVALID_PIN(HttpStatus.BAD_REQUEST, 40008, "PIN 비밀번호가 일치하지 않습니다."),
    MEMO_ALREADY_DECIDE(HttpStatus.BAD_REQUEST, 40009, "이미 처리가 완료된 메모입니다."),
    DB_CONSTRAINT_VIOLATION(HttpStatus.BAD_REQUEST, 40010, "DB 제약조건 오류입니다."),
    ROAD_ADDRESS_LENGTH_OVER(HttpStatus.BAD_REQUEST, 40011, "도로명 주소 최대 길이 초과입니다. (최대255)"),
    LOT_ADDRESS_LENGTH_OVER(HttpStatus.BAD_REQUEST, 40011, "지번 주소 최대 길이 초과입니다. (최대255)"),
    TIME_FORMAT_OUT(HttpStatus.BAD_REQUEST, 40012, "시간 요청이 양식과 맞지 않습니다 (HH:mm)"),
    ANSWER_CONCERN_ESSENTIAL(HttpStatus.BAD_REQUEST, 40013, "답변(ANSWER) 작성 시 고민 ID(concernId)는 필수입니다."),
    CONCERN_STORE_MISMATCH(HttpStatus.BAD_REQUEST, 40014, "해당 고민글은 이 가게의 고민글의 아닙니다."),
    FREE_TYPE_ESSENTIAL(HttpStatus.BAD_REQUEST, 40015, "자유 메모(FREE) 작성 시 카테고리(freeType)는 필수입니다."),
    ;


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
