package com.techatow.url_shortner.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

// todo: fazer testes com dominios proibidos
// todo: pensar sobre se portas
class UrlValidatorTest {

    @Test
    void normalizeUrl_shouldAddHttpsWhenNoProtocol() {
        String result = UrlValidator.normalizeUrl("google.com");

        assertThat(result).isEqualTo("https://google.com");
    }

    @Test
    void normalizeUrl_shouldKeepHttpsWhenPresent() {
        String result = UrlValidator.normalizeUrl("https://google.com");

        assertThat(result).isEqualTo("https://google.com");
    }

    @Test
    void normalizeUrl_shouldKeepHttpWhenPresent() {
        String result = UrlValidator.normalizeUrl("http://google.com");

        assertThat(result).isEqualTo("http://google.com");
    }

    @Test
    void normalizeUrl_shouldTrimWhitespace() {
        String result = UrlValidator.normalizeUrl("  https://google.com  ");

        assertThat(result).isEqualTo("https://google.com");
    }

    @Test
    void normalizeUrl_shouldAcceptUrlWithPath() {
        String result = UrlValidator.normalizeUrl("google.com/search?q=test");

        assertThat(result).isEqualTo("https://google.com/search?q=test");
    }

    @Test
    void normalizeUrl_shouldAcceptUrlWithPort() {
        String result = UrlValidator.normalizeUrl("globo.com:8080");

        assertThat(result).isEqualTo("https://globo.com:8080");
    }

    @Test
    void normalizeUrl_shouldAcceptSubdomain() {
        String result = UrlValidator.normalizeUrl("api.example.com");

        assertThat(result).isEqualTo("https://api.example.com");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void normalizeUrl_shouldThrowExceptionWhenNullOrBlank(String input) {
        assertThatThrownBy(() -> UrlValidator.normalizeUrl(input))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("URL vazia");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ftp://example.com", "file://path/to/file", "javascript:alert(1)"})
    void normalizeUrl_shouldThrowExceptionWhenUnsupportedProtocol(String input) {
        assertThatThrownBy(() -> UrlValidator.normalizeUrl(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Protocolo não suportado. Use http ou https");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ht!tp://example.com", "https://", "http://"})
    void normalizeUrl_shouldThrowExceptionWhenMalformed(String input) {
        assertThatThrownBy(() -> UrlValidator.normalizeUrl(input))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("URL");
    }

    @Test
    void normalizeUrl_shouldAcceptComplexUrl() {
        String input = "example.com/path/to/resource?param1=value1&param2=value2#section";
        String result = UrlValidator.normalizeUrl(input);

        assertThat(result).isEqualTo(
                "https://example.com/path/to/resource?param1=value1&param2=value2#section");
    }

    @Test
    void normalizeUrl_shouldAcceptUrlWithDashes() {
        String result = UrlValidator.normalizeUrl("my-site.com");

        assertThat(result).isEqualTo("https://my-site.com");
    }

    @Test
    void normalizeUrl_shouldAcceptUrlWithNumbers() {
        String result = UrlValidator.normalizeUrl("site123.com");

        assertThat(result).isEqualTo("https://site123.com");
    }
}
