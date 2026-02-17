package com.techatow.url_shortner.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.techatow.url_shortner.dtos.UrlDetailsResponse;
import com.techatow.url_shortner.exceptions.UrlNotFoundException;
import com.techatow.url_shortner.services.ShortenedUrlService;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShortenedUrlService urlService;

    private UrlDetailsResponse buildResponse(String shortCode, String originalUrl) {
        return new UrlDetailsResponse(1L, shortCode, originalUrl,
                "http://localhost:8080/" + shortCode, 0L, LocalDateTime.now(), null,
                LocalDateTime.now().plusDays(7), false);
    }

    @Nested
    class ShortenUrl {

        @Test
        void shouldReturn201WithShortUrl() throws Exception {
            UrlDetailsResponse response = buildResponse("abc123", "https://google.com");
            when(urlService.shortenUrl("https://google.com")).thenReturn(response);

            mockMvc.perform(post("/api/urls").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"url\":\"https://google.com\"}")).andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/urls/abc123"))
                    .andExpect(jsonPath("$.shortCode").value("abc123"))
                    .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/abc123"))
                    .andExpect(jsonPath("$.originalUrl").value("https://google.com"));
        }

        @Test
        void shouldReturn400WhenUrlIsBlank() throws Exception {
            mockMvc.perform(post("/api/urls").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"url\":\"\"}")).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.url").isArray());
        }

        @Test
        void shouldReturn400WhenUrlIsTooLong() throws Exception {
            String longUrl = "https://example.com/" + "a".repeat(2100);

            mockMvc.perform(post("/api/urls").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"url\":\"" + longUrl + "\"}")).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.url").isArray());
        }
    }

    @Nested
    class GetStats {

        @Test
        void shouldReturn200WithUrlDetails() throws Exception {
            UrlDetailsResponse response = buildResponse("abc123", "https://google.com");
            when(urlService.getStats("abc123")).thenReturn(response);

            mockMvc.perform(get("/api/urls/abc123")).andExpect(status().isOk())
                    .andExpect(jsonPath("$.shortCode").value("abc123"))
                    .andExpect(jsonPath("$.clicks").value(0));
        }

        @Test
        void shouldReturn404WhenShortCodeNotFound() throws Exception {
            when(urlService.getStats("noop00"))
                    .thenThrow(new UrlNotFoundException("Short code não encontrado: noop00"));

            mockMvc.perform(get("/api/urls/noop00")).andExpect(status().isNotFound());
        }
    }

    @Nested
    class ListUrls {

        @Test
        void shouldReturn200WithPagedResults() throws Exception {
            UrlDetailsResponse response = buildResponse("abc123", "https://google.com");
            Page<UrlDetailsResponse> page = new PageImpl<>(List.of(response),
                    PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt")), 1);
            when(urlService.listUrls(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/api/urls")).andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].shortCode").value("abc123"))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        void shouldReturn200WithEmptyPage() throws Exception {
            when(urlService.listUrls(any(Pageable.class))).thenReturn(Page.empty());

            mockMvc.perform(get("/api/urls")).andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        void shouldRespectPaginationParameters() throws Exception {
            mockMvc.perform(get("/api/urls").param("page", "1").param("size", "10").param("sort",
                    "clicks,asc")).andExpect(status().isOk());

            verify(urlService).listUrls(argThat(
                    pageable -> pageable.getPageNumber() == 1 && pageable.getPageSize() == 10
                            && pageable.getSort().getOrderFor("clicks") != null));
        }
    }

    @Nested
    class DeleteUrl {

        @Test
        void shouldReturn204WhenDeleted() throws Exception {
            doNothing().when(urlService).deleteUrl("abc123");

            mockMvc.perform(delete("/api/urls/abc123")).andExpect(status().isNoContent());

            verify(urlService).deleteUrl("abc123");
        }

        @Test
        void shouldReturn404WhenShortCodeNotFound() throws Exception {
            doThrow(new UrlNotFoundException("Short code não encontrado")).when(urlService)
                    .deleteUrl("noop00");

            mockMvc.perform(delete("/api/urls/noop00")).andExpect(status().isNotFound());
        }
    }
}
