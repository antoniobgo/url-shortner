package com.techatow.url_shortner.handlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.techatow.url_shortner.dtos.ShortenUrlRequest;
import com.techatow.url_shortner.exceptions.ShortCodeGenerationException;
import com.techatow.url_shortner.exceptions.UrlExpiredException;
import com.techatow.url_shortner.exceptions.UrlNotFoundException;
import jakarta.validation.Valid;

@WebMvcTest(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class TestConfig {
        @RestController
        static class TestController {
            @GetMapping("/test/not-found")
            void notFound() {
                throw new UrlNotFoundException("Short code não encontrado: test");
            }

            @GetMapping("/test/expired")
            void expired() {
                throw new UrlExpiredException("URL expirada");
            }

            @GetMapping("/test/generation-failed")
            void generationFailed() {
                throw new ShortCodeGenerationException("Falha ao gerar");
            }

            @PostMapping("/test/validation")
            void validation(@Valid @RequestBody ShortenUrlRequest request) {}

            @GetMapping("/test/unexpected")
            void unexpected() {
                throw new RuntimeException("Erro inesperado");
            }
        }
    }

    @Test
    void shouldReturn404WhenUrlNotFound() throws Exception {
        mockMvc.perform(get("/test/not-found")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Short code não encontrado: test"));
    }

    @Test
    void shouldReturn410WhenUrlExpired() throws Exception {
        mockMvc.perform(get("/test/expired")).andExpect(status().isGone())
                .andExpect(jsonPath("$.status").value(410));
    }

    @Test
    void shouldReturn503WhenShortCodeGenerationFails() throws Exception {
        mockMvc.perform(get("/test/generation-failed")).andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503));
    }

    @Test
    void shouldReturn400WhenValidationFails() throws Exception {
        mockMvc.perform(post("/test/validation").contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"\"}")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.url").isArray());
    }

    @Test
    void shouldReturn500WhenUnexpectedError() throws Exception {
        mockMvc.perform(get("/test/unexpected")).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Erro interno do servidor"));
    }
}
