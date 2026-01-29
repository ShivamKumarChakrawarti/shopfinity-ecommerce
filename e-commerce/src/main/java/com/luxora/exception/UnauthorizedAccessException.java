package com.luxora.exception;

public class UnauthorizedAccessException extends BusinessException {
    public UnauthorizedAccessException(String message){
        super(message);
    }
}
