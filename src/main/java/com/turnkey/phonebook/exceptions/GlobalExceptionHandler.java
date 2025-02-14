package com.turnkey.phonebook.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        APIError apiError = new APIError();
        apiError.setTimestamp(String.valueOf(LocalDateTime.now()));
        apiError.setStatusCode(400);
        apiError.setMessage(e.getMessage());
        apiError.setPath(request.getRequestURI());
        apiError.setErrors(
                e.getBindingResult()
                       .getAllErrors()
                       .stream()
                       .map(DefaultMessageSourceResolvable::getDefaultMessage)
                       .collect(Collectors.toList())
        );

        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIError> handleAPIException(APIException e, HttpServletRequest request) {
        APIError apiError = new APIError();
        apiError.setTimestamp(String.valueOf(LocalDateTime.now()));
        apiError.setStatusCode(e.getStatusCode());
        apiError.setMessage(e.getMessage());
        apiError.setPath(request.getRequestURI());

        return ResponseEntity.status(e.getStatusCode()).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIError> handleException(Exception e, HttpServletRequest request) {
        APIError apiError = new APIError();
        apiError.setTimestamp(String.valueOf(LocalDateTime.now()));
        apiError.setStatusCode(500);
        apiError.setMessage("An unexpected error occurred.");
        apiError.setPath(request.getRequestURI());

        return ResponseEntity.status(500).body(apiError);
    }

}
