package com.techatow.url_shortner.exceptions;

public class UrlExpiredException extends RuntimeException {
    public UrlExpiredException(String message) {
        super(message);
    }
}
