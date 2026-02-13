package com.techatow.url_shortner.dtos;

public record ShortenUrlResponse(String shortCode, String originalUrl, String shortUrl) {
}
