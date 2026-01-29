package com.luxora.response;

import lombok.Data;

@Data
public class ApiResponse<T>{
    private String message;
    private boolean success;
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(String message, boolean success, T data) {
        this.message = message;
        this.success = success;
        this.data = data;
    }
}
