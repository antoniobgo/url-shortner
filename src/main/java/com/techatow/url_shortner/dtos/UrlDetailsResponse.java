package com.techatow.url_shortner.dtos;

import java.time.LocalDateTime;

public record UrlDetailsResponse(Long id, String shortCode, String originalUrl, String shortUrl,
        Long clicks, LocalDateTime createdAt, LocalDateTime lastAccessedAt, LocalDateTime expiresAt,
        boolean expired) {
}
