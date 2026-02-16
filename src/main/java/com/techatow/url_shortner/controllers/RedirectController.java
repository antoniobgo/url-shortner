package com.techatow.url_shortner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import com.techatow.url_shortner.services.ShortenedUrlService;

@RestController
public class RedirectController {

    @Autowired
    private ShortenedUrlService urlService;

    @GetMapping("/{shortCode}")
    public RedirectView redirect(@PathVariable String shortCode) {
        String originalUrl = urlService.resolveShortCode(shortCode);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(originalUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);

        return redirectView;
    }

    // TODO: criar método que pegue estatisticas
    // @GetMapping("/api/stats/{shortCode}")
    // public ResponseEntity<UrlStatsResponse> getStats(@PathVariable String shortCode) {
    // ShortenedUrl url = urlService.getStats(shortCode);
    // return ResponseEntity.ok(new UrlStatsResponse(url.getShortCode(), url.getOriginalUrl(),
    // url.getClicks(), url.getCreatedAt(), url.getExpiresAt()));
    // }
}
