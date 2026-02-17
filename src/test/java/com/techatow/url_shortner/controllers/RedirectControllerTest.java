package com.techatow.url_shortner.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.techatow.url_shortner.exceptions.UrlExpiredException;
import com.techatow.url_shortner.exceptions.UrlNotFoundException;
import com.techatow.url_shortner.services.ShortenedUrlService;

@WebMvcTest(RedirectController.class)
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShortenedUrlService urlService;

    @Test
    void shouldRedirectToOriginalUrl() throws Exception {
        when(urlService.resolveShortCode("abc123")).thenReturn("https://google.com");

        mockMvc.perform(get("/abc123")).andExpect(status().isFound())
                .andExpect(redirectedUrl("https://google.com"));

        verify(urlService).resolveShortCode("abc123");
    }

    @Test
    void shouldReturn404WhenShortCodeNotFound() throws Exception {
        when(urlService.resolveShortCode("noop00"))
                .thenThrow(new UrlNotFoundException("Short code não encontrado: noop00"));

        mockMvc.perform(get("/noop00")).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn410WhenUrlExpired() throws Exception {
        when(urlService.resolveShortCode("old123"))
                .thenThrow(new UrlExpiredException("URL expirada"));

        mockMvc.perform(get("/old123")).andExpect(status().isGone());
    }

    @Test
    void shouldHandleUrlsWithSpecialCharacters() throws Exception {
        when(urlService.resolveShortCode("abc123"))
                .thenReturn("https://example.com/path?param=value&other=123#section");

        mockMvc.perform(get("/abc123")).andExpect(status().isFound())
                .andExpect(redirectedUrl("https://example.com/path?param=value&other=123#section"));
    }
}
