package com.example.invoicemanager.exception;

import com.example.invoicemanager.domain.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.time.LocalDateTime.now;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<HttpResponse> handleApiException(ApiException e) {
        log.warn(e.getMessage(), e);
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again.");
    }

    private ResponseEntity<HttpResponse> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .httpStatus(status)
                        .statusCode(status.value())
                        .reason(status.getReasonPhrase())
                        .message(message)
                        .build()
        );
    }
}
