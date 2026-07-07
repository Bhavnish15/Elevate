package com.project.Elevate.userService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException{
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException exception) {
        ApiError apiError = new ApiError(exception.getLocalizedMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException exception) {
        ApiError apiError = new ApiError(exception.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException exception) {
        ApiError apiError = new ApiError(exception.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
