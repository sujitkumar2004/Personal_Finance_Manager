////package com.sujit.personal_Finance_Manager.exception;
////
////import org.springframework.http.HttpStatus;
////import org.springframework.http.ResponseEntity;
////import org.springframework.web.bind.annotation.*;
////import org.springframework.web.context.request.WebRequest;
////
////import java.util.HashMap;
////import java.util.Map;
////
////@RestControllerAdvice
////public class GlobalExceptionHandler {
////
////    @ExceptionHandler(BadRequestException.class)
////    public ResponseEntity<Object> handleBadRequest(BadRequestException ex, WebRequest request) {
////        return error(HttpStatus.BAD_REQUEST, ex);
////    }
////
////    @ExceptionHandler(ConflictException.class)
////    public ResponseEntity<Object> handleConflict(ConflictException ex, WebRequest request) {
////        return error(HttpStatus.CONFLICT, ex);
////    }
////
////    @ExceptionHandler(ResourceNotFoundException.class)
////    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
////        return error(HttpStatus.NOT_FOUND, ex);
////    }
////
////    @ExceptionHandler(Exception.class)
////    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
////        return error(HttpStatus.INTERNAL_SERVER_ERROR, ex);
////    }
////
////    private ResponseEntity<Object> error(HttpStatus status, Exception ex) {
////        Map<String, Object> body = new HashMap<>();
////        body.put("status", status.value());
////        body.put("error", status.getReasonPhrase());
////        body.put("message", ex.getMessage());
////        return new ResponseEntity<>(body, status);
////    }
////}
//package com.sujit.personal_Finance_Manager.exception;
//
//import com.fasterxml.jackson.databind.exc.InvalidFormatException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.context.request.WebRequest;
//
//import java.time.format.DateTimeParseException;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<Object> handleBadRequest(BadRequestException ex, WebRequest request) {
//        return error(HttpStatus.BAD_REQUEST, ex);
//    }
//
//    @ExceptionHandler(ConflictException.class)
//    public ResponseEntity<Object> handleConflict(ConflictException ex, WebRequest request) {
//        return error(HttpStatus.CONFLICT, ex);
//    }
//
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
//        return error(HttpStatus.NOT_FOUND, ex);
//    }
//
//    // ✅ Validation Errors Handler
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getFieldErrors().forEach(error ->
//                errors.put(error.getField(), error.getDefaultMessage())
//        );
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("status", HttpStatus.BAD_REQUEST.value());
//        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
//        body.put("message", "Validation failed");
//        body.put("errors", errors);
//
//        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
//    }
////   for date amd time
//@ExceptionHandler(DateTimeParseException.class)
//public ResponseEntity<Object> handleDateParseException(DateTimeParseException ex, WebRequest request) {
//    Map<String, Object> body = new HashMap<>();
//    body.put("status", HttpStatus.BAD_REQUEST.value());
//    body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
//    body.put("message", "Invalid date format. Expected format: YYYY-MM-DD");
//
//    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
//}
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
//        return error(HttpStatus.INTERNAL_SERVER_ERROR, ex);
//    }
//
//    private ResponseEntity<Object> error(HttpStatus status, Exception ex) {
//        Map<String, Object> body = new HashMap<>();
//        body.put("status", status.value());
//        body.put("error", status.getReasonPhrase());
//        body.put("message", ex.getMessage());
//        return new ResponseEntity<>(body, status);
//    }
//    @ExceptionHandler(com.fasterxml.jackson.databind.exc.InvalidFormatException.class)
//    public ResponseEntity<Object> handleInvalidFormat(InvalidFormatException ex, WebRequest request) {
//        Map<String, Object> body = new HashMap<>();
//        body.put("status", HttpStatus.BAD_REQUEST.value());
//        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
//
//        if (ex.getTargetType().isEnum()) {
//            body.put("message", "Invalid category type. Allowed values: INCOME, EXPENSE");
//        } else {
//            body.put("message", "Invalid input format");
//        }
//
//        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
//    }
//
//
//}



package com.sujit.personal_Finance_Manager.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex, WebRequest request) {
        return error(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflict(ConflictException ex, WebRequest request) {
        return error(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        return error(HttpStatus.NOT_FOUND, ex);
    }

    // ✅ Handle validation errors (e.g. @Valid DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("message", "Validation failed");
        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // ✅ Handle invalid enum (e.g. "type": "INVALID")
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Object> handleInvalidFormat(InvalidFormatException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());

        if (ex.getTargetType().isEnum()) {
            body.put("message", "Invalid category type. Allowed values: INCOME, EXPENSE");
        } else {
            body.put("message", "Invalid input format");
        }

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // ✅ Handle invalid date format
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Object> handleDateParseException(DateTimeParseException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("message", "Invalid date format. Expected format: YYYY-MM-DD");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // ❌ Catch-all fallback handler (keep at the end)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        Throwable cause = ex.getCause();

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());

        if (cause instanceof InvalidFormatException formatEx && formatEx.getTargetType().isEnum()) {
            body.put("message", "Invalid category type. Allowed values: INCOME, EXPENSE");
        } else {
            body.put("message", "Invalid request format or type mismatch");
        }

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Utility method for building response
    private ResponseEntity<Object> error(HttpStatus status, Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, status);
    }
}
