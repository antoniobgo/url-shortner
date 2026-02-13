package com.techatow.url_shortner.utils;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlValidator {

    public static String normalizeUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL inválida");
        }

        String normalized = url.trim();

        // Adiciona https:// se não tiver protocolo
        if (!normalized.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*")) {
            normalized = "https://" + normalized;
        }

        // Valida a URI
        try {
            URI uri = new URI(normalized);
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new IllegalArgumentException("URL malformada");
            }
            // Aceita apenas http e https
            if (!uri.getScheme().matches("^https?$")) {
                throw new IllegalArgumentException("Protocolo não suportado. Use http ou https");
            }
            return normalized;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL inválida: " + e.getMessage());
        }
    }
}
