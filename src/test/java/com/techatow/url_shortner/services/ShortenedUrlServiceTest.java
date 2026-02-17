package com.techatow.url_shortner.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import com.techatow.url_shortner.dtos.UrlDetailsResponse;
import com.techatow.url_shortner.entities.ShortenedUrl;
import com.techatow.url_shortner.exceptions.ShortCodeGenerationException;
import com.techatow.url_shortner.exceptions.UrlExpiredException;
import com.techatow.url_shortner.exceptions.UrlNotFoundException;
import com.techatow.url_shortner.repositories.ShortenedUrlRepository;

@ExtendWith(MockitoExtension.class)
class ShortenedUrlServiceTest {

    @Mock
    private ShortenedUrlRepository urlRepository;

    @InjectMocks
    private ShortenedUrlService urlService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://localhost:8080/");
    }

    // -- Helpers --

    private ShortenedUrl buildUrl(String shortCode, String originalUrl) {
        ShortenedUrl url = new ShortenedUrl(shortCode, originalUrl);
        ReflectionTestUtils.setField(url, "id", 1L);
        ReflectionTestUtils.setField(url, "createdAt", LocalDateTime.now());
        return url;
    }

    private ShortenedUrl buildExpiredUrl(String shortCode, String originalUrl) {
        ShortenedUrl url = buildUrl(shortCode, originalUrl);
        url.setExpiresAt(LocalDateTime.now().minusDays(1));
        return url;
    }

    // -- shortenUrl --

    @Nested
    class ShortenUrl {

        @Test
        void shouldReturnExistingUrlWhenAlreadyShortened() {
            ShortenedUrl existing = buildUrl("abc123", "https://google.com");
            when(urlRepository.findByOriginalUrl("https://google.com"))
                    .thenReturn(Optional.of(existing));

            UrlDetailsResponse response = urlService.shortenUrl("google.com");

            assertThat(response.shortCode()).isEqualTo("abc123");
            assertThat(response.shortUrl()).isEqualTo("http://localhost:8080/abc123");
            verify(urlRepository, never()).save(any());
        }

        @Test
        void shouldCreateNewShortenedUrl() {
            when(urlRepository.findByOriginalUrl(any())).thenReturn(Optional.empty());
            when(urlRepository.existsByShortCode(any())).thenReturn(false);
            when(urlRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            UrlDetailsResponse response = urlService.shortenUrl("google.com");

            assertThat(response.originalUrl()).isEqualTo("https://google.com");
            assertThat(response.shortUrl()).startsWith("http://localhost:8080/");
            assertThat(response.shortCode()).hasSize(6);
            verify(urlRepository).save(any(ShortenedUrl.class));
        }

        @Test
        void shouldThrowWhenUnsupportedProtocol() {
            assertThatThrownBy(() -> urlService.shortenUrl("ftp://example.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Protocolo não suportado");
        }

        @Test
        void shouldThrowWhenBlankUrl() {
            assertThatThrownBy(() -> urlService.shortenUrl("   "))
                    .isInstanceOf(IllegalArgumentException.class).hasMessage("URL vazia");
        }

        @Test
        void shouldThrowShortCodeGenerationExceptionAfterMaxAttempts() {
            when(urlRepository.findByOriginalUrl(any())).thenReturn(Optional.empty());
            when(urlRepository.existsByShortCode(any())).thenReturn(true);

            assertThatThrownBy(() -> urlService.shortenUrl("google.com"))
                    .isInstanceOf(ShortCodeGenerationException.class)
                    .hasMessageContaining("Falha ao gerar código único");

            verify(urlRepository, times(5)).existsByShortCode(any());
        }
    }

    // -- resolveShortCode --

    @Nested
    class ResolveShortCode {

        @Test
        void shouldReturnOriginalUrlAndIncrementClicks() {
            ShortenedUrl url = buildUrl("abc123", "https://google.com");
            when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(url));
            when(urlRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            String result = urlService.resolveShortCode("abc123");

            assertThat(result).isEqualTo("https://google.com");
            assertThat(url.getClicks()).isEqualTo(1L);
            assertThat(url.getLastAccessedAt()).isNotNull();
            verify(urlRepository).save(url);
        }

        @Test
        void shouldThrowUrlNotFoundExceptionWhenCodeDoesNotExist() {
            when(urlRepository.findByShortCode("noop00")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> urlService.resolveShortCode("noop00"))
                    .isInstanceOf(UrlNotFoundException.class).hasMessageContaining("noop00");
        }

        @Test
        void shouldThrowUrlExpiredExceptionWhenUrlIsExpired() {
            ShortenedUrl expired = buildExpiredUrl("old123", "https://google.com");
            when(urlRepository.findByShortCode("old123")).thenReturn(Optional.of(expired));

            assertThatThrownBy(() -> urlService.resolveShortCode("old123"))
                    .isInstanceOf(UrlExpiredException.class).hasMessage("URL expirada");
        }
    }

    // -- getStats --

    @Nested
    class GetStats {

        @Test
        void shouldReturnUrlDetails() {
            ShortenedUrl url = buildUrl("abc123", "https://google.com");
            when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(url));

            UrlDetailsResponse response = urlService.getStats("abc123");

            assertThat(response.shortCode()).isEqualTo("abc123");
            assertThat(response.originalUrl()).isEqualTo("https://google.com");
            assertThat(response.shortUrl()).isEqualTo("http://localhost:8080/abc123");
        }

        @Test
        void shouldThrowWhenShortCodeNotFound() {
            when(urlRepository.findByShortCode("noop00")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> urlService.getStats("noop00"))
                    .isInstanceOf(UrlNotFoundException.class);
        }
    }

    // -- listUrls --

    @Nested
    class ListUrls {

        @Test
        void shouldReturnPagedResults() {
            Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
            ShortenedUrl url = buildUrl("abc123", "https://google.com");
            Page<ShortenedUrl> page = new PageImpl<>(List.of(url), pageable, 1);
            when(urlRepository.findAll(pageable)).thenReturn(page);

            Page<UrlDetailsResponse> result = urlService.listUrls(pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).shortCode()).isEqualTo("abc123");
        }

        @Test
        void shouldReturnEmptyPageWhenNoUrls() {
            Pageable pageable = PageRequest.of(0, 20);
            when(urlRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

            Page<UrlDetailsResponse> result = urlService.listUrls(pageable);

            assertThat(result.getContent()).isEmpty();
        }
    }

    // -- deleteUrl --

    @Nested
    class DeleteUrl {

        @Test
        void shouldDeleteExistingUrl() {
            ShortenedUrl url = buildUrl("abc123", "https://google.com");
            when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(url));

            urlService.deleteUrl("abc123");

            verify(urlRepository).delete(url);
        }

        @Test
        void shouldThrowWhenShortCodeNotFound() {
            when(urlRepository.findByShortCode("noop00")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> urlService.deleteUrl("noop00"))
                    .isInstanceOf(UrlNotFoundException.class).hasMessageContaining("noop00");

            verify(urlRepository, never()).delete(any());
        }
    }
}
