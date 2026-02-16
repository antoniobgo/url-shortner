package com.techatow.url_shortner.dtos;

import java.time.LocalDateTime;

public record UrlStatsResponse(String shortCode, String originalUrl, Long clicks,
        LocalDateTime createdAt, LocalDateTime expiresAt) {
}
