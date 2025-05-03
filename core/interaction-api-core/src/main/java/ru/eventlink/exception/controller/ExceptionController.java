package ru.eventlink.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.eventlink.exception.*;
import ru.eventlink.exception.model.ApiError;

import java.time.LocalDateTime;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleCommonException(Exception e) {
        log.error("500 {} ", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .reason("Internal Server Error")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("409 {} ", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Incorrectly made request")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleDataTimeException(DataTimeException e) {
        log.error("409 {} ", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Incorrectly made request with date and time")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        log.error("404 {} ", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND.name())
                .reason("The required object was not found")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIntegrityViolationException(IntegrityViolationException e) {
        log.error("409 {} ", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.name())
                .reason("Integrity constraint has been violated")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleRestrictionsViolationException(RestrictionsViolationException e) {
        log.error("409 {} ", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.name())
                .reason("For the requested operation the conditions are not met.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("400 {} ", e.getMessage());
        String violations = e.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Incorrectly made request")
                .message(violations)
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("400 {} ", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Incorrectly made request")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleServerUnavailableException(ServerUnavailableException e) {
        log.error("500 {} ", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .reason("Internal Server Error")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        log.error("400 {} ", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("BAD_REQUEST")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(ExceptionUtils.getStackTrace(e))
                .build();
    }

    @ExceptionHandler(CompletionException.class)
    public ResponseEntity<ApiError> handleCompletionException(CompletionException e) {
        Throwable cause = e.getCause();
        switch (cause) {
            case IntegrityViolationException integrityViolationException -> {
                ApiError apiError = handleIntegrityViolationException(integrityViolationException);
                return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
            }
            case NotFoundException notFoundException -> {
                ApiError apiError = handleNotFoundException(notFoundException);
                return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
            }
            case RestrictionsViolationException restrictionsViolationException -> {
                ApiError apiError = handleRestrictionsViolationException(restrictionsViolationException);
                return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
            }
            case null, default -> {
                ApiError apiError = ApiError.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .reason("INTERNAL_SERVER_ERROR")
                        .message(e.getMessage())
                        .timestamp(LocalDateTime.now())
                        .errors(ExceptionUtils.getStackTrace(e))
                        .build();
                return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
