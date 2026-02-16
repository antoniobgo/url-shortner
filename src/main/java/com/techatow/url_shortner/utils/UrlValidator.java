package com.techatow.url_shortner.utils;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Set;

public class UrlValidator {

    private static final Set<String> BLOCKED_DOMAINS = Set.of("localhost", "127.0.0.1", "0.0.0.0",
            "::1", "169.254.169.254", "metadata.google.internal", "metadata", "::ffff:127.0.0.1");

    public static String normalizeUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL vazia");
        }

        String normalized = url.trim();

        if (normalized.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*")) {
            if (!normalized.matches("^https?://.*")) {
                throw new IllegalArgumentException("Protocolo não suportado. Use http ou https");
            }
        } else if (normalized.matches("^[a-zA-Z][a-zA-Z0-9+.-]*:.*")) {
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

            validateSecurity(uri);

            return normalized;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URL inválida: " + e.getMessage());
        }
    }

    private static void validateSecurity(URI uri) {
        String host = uri.getHost().toLowerCase();

        // Verificar lista de bloqueio primeiro (antes de resolver DNS)
        if (BLOCKED_DOMAINS.contains(host)) {
            throw new IllegalArgumentException("URLs internas não são permitidas");
        }

        // Evitar credenciais na URL
        if (uri.getUserInfo() != null) {
            throw new IllegalArgumentException("URL com credenciais não permitida");
        }

        // Tentar resolver DNS, mas não falhar se não existir
        try {
            InetAddress address = InetAddress.getByName(host);
            String resolvedIP = address.getHostAddress();

            // Se resolveu, verificar se é IP privado
            if (BLOCKED_DOMAINS.contains(resolvedIP) || isPrivateIP(resolvedIP)) {
                throw new IllegalArgumentException("URLs internas não são permitidas");
            }
        } catch (UnknownHostException e) {
            // Domínio não existe ou DNS falhou
            // Permitir e deixar falhar no redirect (melhor UX)
            // OU bloquear se preferir ser mais rigoroso
        }
    }

    private static boolean isPrivateIP(String ip) {
        return ip.matches("^127\\..*")
                || ip.matches("^(10|172\\.(1[6-9]|2[0-9]|3[01])|192\\.168)\\..*")
                || ip.matches("^169\\.254\\..*") || ip.matches("^(fc00|fd00|fe80):.*");
    }
}
// public class UrlValidator {

// public static String normalizeUrl(String url) {
// if (url == null || url.isBlank()) {
// throw new IllegalArgumentException("URL vazia");
// }

// String normalized = url.trim();

// // Testa se há protocolo completo (://)
// if (normalized.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*")) {
// // Testa se protocolo completo (://) é http ou https
// if (!normalized.matches("^https?://.*")) {
// throw new IllegalArgumentException("Protocolo não suportado. Use http ou https");
// }
// // Testa se tem : mas não :// - pode ser porta OU protocolo incompleto
// } else if (normalized.matches("^[a-zA-Z][a-zA-Z0-9+.-]*:.*")) {
// // Testa se parece com porta (host:numero)
// if (!normalized.matches("^[^:]+:\\d+.*")) {
// throw new IllegalArgumentException("Protocolo não suportado. Use http ou https");
// }
// normalized = "https://" + normalized;
// } else {
// normalized = "https://" + normalized;
// }

// try {
// URI uri = new URI(normalized);
// if (uri.getScheme() == null || uri.getHost() == null) {
// throw new IllegalArgumentException("URL malformada");
// }
// return normalized;
// } catch (URISyntaxException e) {
// throw new IllegalArgumentException("URL inválida: " + e.getMessage());
// }
// }
// }
