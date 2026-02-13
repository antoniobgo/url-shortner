package com.techatow.url_shortner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.techatow.url_shortner.dtos.ShortenUrlRequest;
import com.techatow.url_shortner.dtos.ShortenUrlResponse;
import com.techatow.url_shortner.services.ShortenedUrlService;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/shorten")
public class UrlController {

    @Autowired
    private ShortenedUrlService urlService;

    @PostMapping
    public ResponseEntity<ShortenUrlResponse> shortenUrl(
            @Valid @RequestBody ShortenUrlRequest request) {
        ShortenUrlResponse response = urlService.shortenUrl(request.url());
        return ResponseEntity.ok(response);
    }


}
