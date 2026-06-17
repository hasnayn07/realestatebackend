package com.realestatebackend.common;

import lombok.*;

@Data @AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
    private String path;
}

