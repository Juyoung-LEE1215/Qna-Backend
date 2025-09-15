package com.example.qnabackend.config;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handle(Exception e) {
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("message", e.getMessage());
        body.put("type", e.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
