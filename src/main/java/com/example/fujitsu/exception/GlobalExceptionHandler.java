package com.example.fujitsu.exception;

import com.example.fujitsu.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler that converts exceptions into consistent API error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles forbidden vehicle usage errors.
     *
     * @param e thrown exception
     * @return error response
     */
    @ExceptionHandler(ForbiddenVehicleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleForbiddenVehicleException(ForbiddenVehicleException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Handles invalid request parameters (e.g. wrong enum values).
     *
     * @param e thrown exception
     * @return error response
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return new ErrorResponse("Invalid city or vehicle type");
    }

    /**
     * Handles general runtime exceptions.
     *
     * @param e thrown exception
     * @return error response
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRuntimeException(RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Handles unexpected system errors.
     *
     * @param e thrown exception
     * @return error response
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception e) {
        return new ErrorResponse("Unexpected error occurred");
    }
}