package com.techatow.url_shortner.handlers;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.techatow.url_shortner.dtos.CustomErrorResponse;
import com.techatow.url_shortner.exceptions.ShortCodeGenerationException;
import com.techatow.url_shortner.exceptions.UrlExpiredException;
import com.techatow.url_shortner.exceptions.UrlNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ShortCodeGenerationException.class)
    public ResponseEntity<CustomErrorResponse> handleShortCodeGenerationException(
            ShortCodeGenerationException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return buildErrorResponse(e, status, request);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleUrlNotFoundException(UrlNotFoundException e,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return buildErrorResponse(e, status, request);
    }

    @ExceptionHandler(UrlExpiredException.class)
    public ResponseEntity<CustomErrorResponse> handleUrlExpiredException(UrlExpiredException e,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.GONE;
        return buildErrorResponse(e, status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGeneral(Exception e,
            HttpServletRequest request) {
        logger.error("Erro inesperado capturado", e);
        return buildErrorResponse(new RuntimeException("Erro interno do servidor"),
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<CustomErrorResponse> buildErrorResponse(Exception e, HttpStatus status,
            HttpServletRequest request) {
        CustomErrorResponse err = new CustomErrorResponse(Instant.now(), status.value(),
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
}
