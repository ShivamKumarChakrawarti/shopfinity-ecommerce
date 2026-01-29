package com.luxora.exception;

import com.luxora.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidOtp(InvalidOtpException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(ex.getMessage(), false, null));
    }

    @ExceptionHandler(OtpNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleOtpNotFound(OtpNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(ex.getMessage(), false, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Something went wrong", false, null));
    }

    @ExceptionHandler(SellerNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleSellerNotFound(SellerNotFoundException ex){
        log.warn("SellerNotFoundException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(ex.getMessage(), false, null));
    }

    @ExceptionHandler(SellerAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleSellerAlreadyExists(SellerAlreadyExistsException ex){
        log.warn("SellerAlreadyExistsException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(ex.getMessage(), false, null));
    }

    @ExceptionHandler(InvalidSellerStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidSellerState(InvalidSellerStateException ex){
        log.warn("InvalidSellerStateException: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(ex.getMessage(), false, null));
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedAccess(UnauthorizedAccessException ex){
        log.warn("UnauthorizedAccessException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(ex.getMessage(), false, null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        log.warn("Validation error: {}", errors);

        return ResponseEntity.badRequest()
                .body(new ApiResponse<>("Validation failed", false, errors));
    }
}
