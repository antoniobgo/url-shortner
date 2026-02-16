package com.techatow.url_shortner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.techatow.url_shortner.dtos.ShortenUrlRequest;
import com.techatow.url_shortner.dtos.ShortenUrlResponse;
import com.techatow.url_shortner.dtos.UrlStatsResponse;
import com.techatow.url_shortner.services.ShortenedUrlService;
import jakarta.validation.Valid;


@RestController
@RequestMapping(path = "/api/urls")
public class UrlController {

    @Autowired
    private ShortenedUrlService urlService;

    @PostMapping
    public ResponseEntity<ShortenUrlResponse> shortenUrl(
            @Valid @RequestBody ShortenUrlRequest request) {
        ShortenUrlResponse response = urlService.shortenUrl(request.url());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlStatsResponse> getStats(@PathVariable String shortCode) {
        UrlStatsResponse urlResponseEntity = urlService.getStats(shortCode);
        return ResponseEntity.ok(urlResponseEntity);
    }

    @GetMapping
    public ResponseEntity<Page<UrlStatsResponse>> listUrls(@PageableDefault(size = 20,
            sort = "createdAt",
            direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {

        Page<UrlStatsResponse> urls = urlService.listUrls(pageable);
        return ResponseEntity.ok(urls);
    }



}
