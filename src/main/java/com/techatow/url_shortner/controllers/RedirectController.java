package com.techatow.url_shortner.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import com.techatow.url_shortner.services.ShortenedUrlService;

@RestController
public class RedirectController {

    private final ShortenedUrlService urlService;

    public RedirectController(ShortenedUrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/{shortCode}")
    public RedirectView redirect(@PathVariable String shortCode) {
        String originalUrl = urlService.resolveShortCode(shortCode);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(originalUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);

        return redirectView;
    }

}
