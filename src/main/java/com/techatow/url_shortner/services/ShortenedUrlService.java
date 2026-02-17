package com.techatow.url_shortner.services;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.techatow.url_shortner.dtos.ShortenUrlResponse;
import com.techatow.url_shortner.dtos.UrlStatsResponse;
import com.techatow.url_shortner.entities.ShortenedUrl;
import com.techatow.url_shortner.exceptions.ShortCodeGenerationException;
import com.techatow.url_shortner.exceptions.UrlExpiredException;
import com.techatow.url_shortner.exceptions.UrlNotFoundException;
import com.techatow.url_shortner.repositories.ShortenedUrlRepository;
import com.techatow.url_shortner.utils.ShortCodeGenerator;
import com.techatow.url_shortner.utils.UrlValidator;

@Service
public class ShortenedUrlService {

    @Autowired
    private ShortenedUrlRepository urlRepository;

    @Value("${app.base-url:http://localhost:8080/}")
    private String baseUrl;

    @Transactional
    public ShortenUrlResponse shortenUrl(String url) {
        String normalizedUrl = UrlValidator.normalizeUrl(url);

        Optional<ShortenedUrl> optionalShortenedUrl =
                urlRepository.findByOriginalUrl(normalizedUrl);
        if (optionalShortenedUrl.isPresent()) {
            ShortenedUrl shortenedUrl = optionalShortenedUrl.get();
            return new ShortenUrlResponse(shortenedUrl.getShortCode(),
                    shortenedUrl.getOriginalUrl(), baseUrl + shortenedUrl.getShortCode());
        }

        final String shortCode = generateUniqueCode();
        ShortenedUrl shortenedUrl = new ShortenedUrl(shortCode, normalizedUrl);
        urlRepository.save(shortenedUrl);

        return new ShortenUrlResponse(shortenedUrl.getShortCode(), shortenedUrl.getOriginalUrl(),
                baseUrl + shortCode);
    }

    @Transactional
    public String resolveShortCode(String shortCode) {
        ShortenedUrl url = urlRepository.findByShortCode(shortCode).orElseThrow(
                () -> new UrlNotFoundException("Short code não encontrado: " + shortCode));

        if (url.isExpired()) {
            throw new UrlExpiredException("URL expirada");
        }

        url.setClicks(url.getClicks() + 1);
        url.setLastAccessedAt(LocalDateTime.now());
        urlRepository.save(url);

        return url.getOriginalUrl();
    }

    private String generateUniqueCode() {
        int maxAttempts = 5;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            String code = ShortCodeGenerator.generateRandomCode();
            if (!urlRepository.existsByShortCode(code)) {
                return code;
            }
        }
        throw new ShortCodeGenerationException(
                "Falha ao gerar código único após " + maxAttempts + " tentativas");
    }

    @Transactional(readOnly = true)
    public UrlStatsResponse getStats(String shortCode) {
        ShortenedUrl shortenedUrl = urlRepository.findByShortCode(shortCode).orElseThrow(
                () -> new UrlNotFoundException("Url associada ao short code não encontrada"));
        return new UrlStatsResponse(shortenedUrl.getId(), shortenedUrl.getShortCode(),
                shortenedUrl.getOriginalUrl(), baseUrl + shortenedUrl.getShortCode(),
                shortenedUrl.getClicks(), shortenedUrl.getCreatedAt(),
                shortenedUrl.getLastAccessedAt(), shortenedUrl.getExpiresAt(),
                shortenedUrl.isExpired());
    }

    @Transactional(readOnly = true)
    public Page<UrlStatsResponse> listUrls(Pageable pageable) {
        return urlRepository.findAll(pageable)
                .map(url -> new UrlStatsResponse(url.getId(), url.getShortCode(),
                        url.getOriginalUrl(), baseUrl + url.getShortCode(), url.getClicks(),
                        url.getCreatedAt(), url.getLastAccessedAt(), url.getExpiresAt(),
                        url.isExpired()));
    }

    @Transactional
    public void deleteUrl(String shortCode) {
        ShortenedUrl url = urlRepository.findByShortCode(shortCode).orElseThrow(
                () -> new UrlNotFoundException("Short code não encontrado: " + shortCode));

        urlRepository.delete(url);
    }
}
