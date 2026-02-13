package com.techatow.url_shortner.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.techatow.url_shortner.entities.ShortenedUrl;

public interface ShortenedUrlRepository extends JpaRepository<ShortenedUrl, Long> {
    public boolean existsByShortCode(String shortCode);

    Optional<ShortenedUrl> findByShortCode(String shortCode);
}
