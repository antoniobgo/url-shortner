package com.techatow.url_shortner.controllers;

import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.techatow.url_shortner.dtos.ShortenUrlRequest;
import com.techatow.url_shortner.dtos.UrlDetailsResponse;
import com.techatow.url_shortner.services.ShortenedUrlService;
import jakarta.validation.Valid;


@RestController
@RequestMapping(path = "/api/urls")
public class UrlController {

    private final ShortenedUrlService urlService;

    public UrlController(ShortenedUrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    public ResponseEntity<UrlDetailsResponse> shortenUrl(
            @Valid @RequestBody ShortenUrlRequest request) {
        UrlDetailsResponse response = urlService.shortenUrl(request.url());
        URI location = URI.create("/api/urls/" + response.shortCode());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlDetailsResponse> getStats(@PathVariable String shortCode) {
        UrlDetailsResponse urlResponseEntity = urlService.getStats(shortCode);
        return ResponseEntity.ok(urlResponseEntity);
    }

    @GetMapping
    public ResponseEntity<Page<UrlDetailsResponse>> listUrls(@PageableDefault(size = 20,
            sort = "createdAt", direction = Direction.DESC) Pageable pageable) {

        Page<UrlDetailsResponse> urls = urlService.listUrls(pageable);
        return ResponseEntity.ok(urls);
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        urlService.deleteUrl(shortCode);
        return ResponseEntity.noContent().build();
    }



}
