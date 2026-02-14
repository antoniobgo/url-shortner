package com.techatow.url_shortner.utils;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlValidator {

    public static String normalizeUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL vazia");
        }

        String normalized = url.trim();

        // Testa se há protocolo completo (://)
        if (normalized.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*")) {
            // Testa se protocolo completo (://) é http ou https
            if (!normalized.matches("^https?://.*")) {
                throw new IllegalArgumentException("Protocolo não suportado. Use http ou https");
            }
            // Testa se tem : mas não :// - pode ser porta OU protocolo incompleto
        } else if (normalized.matches("^[a-zA-Z][a-zA-Z0-9+.-]*:.*")) {
            // Testa se parece com porta (host:numero)
            if (!normalized.matches("^[^:]+:\\d+.*")) {
                throw new IllegalArgumentException("Protocolo não suportado. Use http ou https");
            }
            normalized = "https://" + normalized;
        } else {
            normalized = "https://" + normalized;
        }

        try {
            URI uri = new URI(normalized);
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new IllegalArgumentException("URL malformada");
            }
            return normalized;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL inválida: " + e.getMessage());
        }
    }
}
