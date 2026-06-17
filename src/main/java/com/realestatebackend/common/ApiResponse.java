package com.realestatebackend.common;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    public static <T> ApiResponse<T> ok(T data){ return new ApiResponse<>(true, data, null); }
}
