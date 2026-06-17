package com.realestatebackend.common.exception;

import com.realestatebackend.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> unauthorized(BadCredentialsException ex, HttpServletRequest req){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("UNAUTHORIZED", ex.getMessage(), req.getRequestURI()));
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(NotFoundException ex, HttpServletRequest req){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", ex.getMessage(), req.getRequestURI()));
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequest(BadRequestException ex, HttpServletRequest req){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("BAD_REQUEST", ex.getMessage(), req.getRequestURI()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req){
        var msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField()+": "+e.getDefaultMessage()).findFirst().orElse("Validation error");
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse("VALIDATION_ERROR", msg, req.getRequestURI()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> generic(Exception ex, HttpServletRequest req){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", ex.getMessage(), req.getRequestURI()));
    }
}