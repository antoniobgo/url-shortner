package com.techatow.url_shortner.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.techatow.url_shortner.dtos.ShortenUrlResponse;
import com.techatow.url_shortner.entities.ShortenedUrl;
import com.techatow.url_shortner.repositories.ShortenedUrlRepository;
import com.techatow.url_shortner.utils.ShortCodeGenerator;

@Service
public class ShortenedUrlService {

    @Autowired
    private ShortenedUrlRepository urlRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Transactional
    public ShortenUrlResponse shortenUrl(String url) {

        final String shortCode = generateUniqueCode();
        ShortenedUrl shortenedUrl = new ShortenedUrl(shortCode, url);
        urlRepository.save(shortenedUrl);

        final String shortUrl = baseUrl + shortCode;

        return new ShortenUrlResponse(shortenedUrl.getShortCode(), shortenedUrl.getOriginalUrl(),
                shortUrl);
    }

    private String generateUniqueCode() {
        int maxAttempts = 5;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            String code = ShortCodeGenerator.generateRandomCode();
            if (!urlRepository.existsByShortCode(code)) {
                return code;
            }
        }
        // melhorar excessao
        throw new IllegalStateException(
                "Falha ao gerar código único após " + maxAttempts + " tentativas");
    }
}
