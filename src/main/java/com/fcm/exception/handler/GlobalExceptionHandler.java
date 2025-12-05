package com.fcm.exception.handler;

import com.fcm.dto.error.ApiErrorResponse;
import com.fcm.enums.ErrorCode;
import com.fcm.exception.FirebaseInternalException;
import com.fcm.exception.RecordNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {

        ErrorCode errorCode = ErrorCode.MALFORMED_JSON;
        List<String> details = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        ApiErrorResponse responseBody = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .success(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .error(errorCode)
                .cause(ex.getCause() != null ? ex.getCause().getClass().getName() : ex.getClass().getName())
                .eCode(errorCode.getECode())
                .message(errorCode.getEMessage())
                .details(details)
                .debugInformation(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.badRequest().body(responseBody);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpServletRequest request) {

        ErrorCode errorCode = ErrorCode.MALFORMED_JSON;
        List<String> details = ex.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(MessageSourceResolvable::getDefaultMessage)).toList();

        ApiErrorResponse responseBody = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .success(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .error(errorCode)
                .cause(ex.getCause() != null ? ex.getCause().getClass().getName() : ex.getClass().getName())
                .eCode(errorCode.getECode())
                .message(errorCode.getEMessage())
                .details(details)
                .debugInformation(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.badRequest().body(responseBody);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {

        ErrorCode errorCode = ErrorCode.MALFORMED_JSON;

        ApiErrorResponse responseBody = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .success(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .error(errorCode)
                .cause(ex.getCause() != null ? ex.getCause().getClass().getName() : ex.getClass().getName())
                .eCode(errorCode.getECode())
                .message(errorCode.getEMessage())
                .details(List.of("Unable to parse request body!"))
                .debugInformation(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.badRequest().body(responseBody);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRecordNotFoundException(RecordNotFoundException ex, HttpServletRequest request) {

        ErrorCode errorCode = ErrorCode.NOT_FOUND;
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .success(false)
                .status(HttpStatus.NOT_FOUND.value())
                .error(errorCode)
                .cause(ex.getCause() != null ? ex.getCause().getClass().getName() : ex.getClass().getName())
                .eCode(errorCode.getECode())
                .message(errorCode.getEMessage())
                .details(List.of(ex.getMessage()))
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityException(DataIntegrityViolationException ex, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.SQL_EXCEPTION;

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .success(false)
                .status(HttpStatus.CONFLICT.value())
                .error(errorCode)
                .cause(ex.getCause() != null ? ex.getCause().getClass().getName() : ex.getClass().getName())
                .eCode(errorCode.getECode())
                .message(errorCode.getEMessage())
                .details(List.of(ex.getMessage()))
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FirebaseInternalException.class)
    public ResponseEntity<ApiErrorResponse> handleFirebaseException(FirebaseInternalException ex, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.FIREBASE_EXCEPTION;

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .success(false)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(errorCode)
                .cause(ex.getCause() != null ? ex.getCause().getClass().getName() : ex.getClass().getName())
                .eCode(errorCode.getECode())
                .message(errorCode.getEMessage())
                .details(List.of(ex.getMessage()))
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
