package com.techatow.url_shortner.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.techatow.url_shortner.dtos.ShortenUrlResponse;
import com.techatow.url_shortner.entities.ShortenedUrl;
import com.techatow.url_shortner.repositories.ShortenedUrlRepository;

@Service
public class ShortenedUrlService {

    @Autowired
    private ShortenedUrlRepository urlRepository;

    public ShortenUrlResponse shortenUrl(String url) {
        // generate shortenUrl
        // save shortenurl
        final String shortCode = "abcdef";
        final String baseUrl = "appbaseurl.com/";
        final String shortUrl = baseUrl.concat(shortCode);
        ShortenedUrl shortenedUrl = new ShortenedUrl(shortCode, url);
        urlRepository.save(shortenedUrl);
        return new ShortenUrlResponse(shortenedUrl.getShortCode(), shortenedUrl.getOriginalUrl(),
                shortUrl);
    }
}
