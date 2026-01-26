package com.posit.posit.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 예외
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.error("[CustomException] status={} code={} message={} ",
                errorCode.getHttpStatus(), errorCode.getCode(), e.getMessage(), e);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.fail(errorCode));
    }

    // Bean Validation 실패 (@Valid @RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<ErrorResponse.FieldErrorDetail> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> ErrorResponse.FieldErrorDetail.of(err.getField(), err.getDefaultMessage()))
                .toList();

        log.warn("[Validation] {}", errors);

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.fail(ErrorCode.BAD_REQUEST, errors));
    }

    // HTTP 메서드 불일치 (405)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("[MethodNotAllowed] {}", e.getMessage(), e);

        return ResponseEntity
                .status(ErrorCode.METHOD_NOT_ALLOWED.getHttpStatus())
                .body(ErrorResponse.fail(ErrorCode.METHOD_NOT_ALLOWED));
    }

    // RequestBody JSON 타입 불일치
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("[HttpMessageNotReadable] {}", e.getMessage(), e);

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.fail(ErrorCode.BAD_REQUEST));
    }

    // 기타 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("[Unhandled Exception]", e);

        return ResponseEntity
                .status(ErrorCode.INTERNAL_ERROR.getHttpStatus())
                .body(ErrorResponse.fail(ErrorCode.INTERNAL_ERROR));
    }
}
