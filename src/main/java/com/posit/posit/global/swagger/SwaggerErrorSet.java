package com.posit.posit.global.swagger;

import com.posit.posit.global.error.ErrorCode;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.posit.posit.global.error.ErrorCode.*;

@Getter
public enum SwaggerErrorSet {
    // 회원가입
    AUTH_SIGNUP(setOf(
            PHONE_VERIFICATION_NOT_FOUND,
            PHONE_VERIFICATION_EXPIRED,
            PHONE_NOT_VERIFIED,
            DUPLICATE_LOGIN_ID,
            DUPLICATE_PHONE,
            STORE_NOT_FOUND
    )),

    // Login
    AUTH_LOGIN(setOf(
            USER_NOT_FOUND,
            INVALID_PASSWORD
    )),

    // Rotate
    AUTH_ROTATE(setOf(
            INVALID_REFRESH_TOKEN
    )),

    // Logout
    AUTH_LOGOUT(setOf(
            INVALID_REFRESH_TOKEN
    )),

    // 휴대폰 인증 요청
    PHONE_VERIFY_REQUEST(setOf(
            BAD_REQUEST
    )),

    // 휴대폰 인증 확인
    PHONE_VERIFY_CONFIRM(setOf(
            PHONE_VERIFICATION_NOT_FOUND,
            PHONE_VERIFICATION_EXPIRED,
            PHONE_VERIFICATION_ATTEMPT_LIMIT,
            PHONE_VERIFICATION_CODE_MISMATCH
    )),

    MAP_DETAIL(setOf(
        STORE_NOT_FOUND
    )),

    MEMO_ADOPT(setOf(
            STORE_NOT_FOUND,
            MEMO_NOT_FOUND,
            MEMO_STORE_FORBIDDEN,
            MEMO_DECISION_DUPLICATE,
            TEMPLATE_NOT_FOUND,
            COUPON_TEMPLATE_FORBIDDEN
    )),
    MEMO_REJECT(setOf(
            STORE_NOT_FOUND,
            MEMO_NOT_FOUND,
            MEMO_STORE_FORBIDDEN,
            MEMO_DECISION_DUPLICATE
    )),

    STORE_UPDATE(setOf(
            STORE_NOT_FOUND,
            FILTER_CODE_NOT_FOUND,
            CONVINCE_CODE_NOT_FOUND,
            ROAD_ADDRESS_LENGTH_OVER,
            LOT_ADDRESS_LENGTH_OVER,
            TIME_FORMAT_OUT
    )),

    CONVINCE_UPDATE(setOf(
            STORE_NOT_FOUND,
            CONVINCE_CODE_NOT_FOUND
    )),

    DEFAULT(setOf());

    private final Set<ErrorCode> codes;

    SwaggerErrorSet(Set<ErrorCode> codes) {
        // 전역 공통
        codes.addAll(setOf(
                BAD_REQUEST,
                METHOD_NOT_ALLOWED,
                INTERNAL_ERROR
        ));
        this.codes = codes;
    }

    private static Set<ErrorCode> setOf(ErrorCode... codes) {
        return new LinkedHashSet<>(Set.of(codes));
    }
}
