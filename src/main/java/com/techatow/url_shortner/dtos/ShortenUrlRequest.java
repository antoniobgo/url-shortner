package com.techatow.url_shortner.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ShortenUrlRequest(@NotBlank(message = "URL não pode estar vazia") @Size(max = 2048,
        message = "URL muito longa") String url) {
}
