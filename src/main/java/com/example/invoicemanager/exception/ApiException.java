package com.example.invoicemanager.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
