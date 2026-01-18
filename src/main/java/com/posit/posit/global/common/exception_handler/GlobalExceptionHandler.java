package com.posit.posit.global.common.exception_handler;


import com.posit.posit.global.common.exception.CustomException;
import com.posit.posit.global.common.response.ApiResponse;
import com.posit.posit.global.common.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class GlobalExceptionHandler {
    private String resolveMessage(String customMessage, String defaultMessage) {
        return (customMessage != null && !customMessage.isBlank()) ? customMessage : defaultMessage;
    }

    // 커스텀 예외
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.error("[CustomException] {} - {} - {}", errorCode.getHttpStatus(), errorCode.getCode(), e);

        return new ResponseEntity<>(ApiResponse.fail(errorCode), errorCode.getHttpStatus());
    }


    // Bean Validation 실패 (@Valid @RequestBody)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String detailMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.error("[Validation] {}", e);

        return ApiResponse.fail(ErrorCode.BAD_REQUEST);
    }

    // HTTP 메서드 불일치 (405)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResponse<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.error("[MethodNotAllowed] {}", e.getMessage(), e);

        return ApiResponse.fail(ErrorCode.METHOD_NOT_ALLOWED);
    }

    // RequestBody JSON 타입 불일치
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error("[HttpMessageNotReadable] {}", e.getMessage(), e);
        return ApiResponse.fail(ErrorCode.BAD_REQUEST);
    }

    // TODO: 더 세분화하고싶은 예외는 직접 추가

    // 기타 모든 예외
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("[Unhandled Exception] {}", e);

        return ApiResponse.fail(ErrorCode.INTERNAL_ERROR);
    }
}
