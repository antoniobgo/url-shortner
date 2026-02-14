package com.techatow.url_shortner.utils;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ShortCodeGeneratorTest {

    @Test
    void generateRandomCode_shouldReturnCodeWithCorrectLength() {
        String code = ShortCodeGenerator.generateRandomCode();

        assertThat(code).hasSize(6);
    }

    @Test
    void generateRandomCode_shouldContainOnlyValidCharacters() {
        String code = ShortCodeGenerator.generateRandomCode();

        assertThat(code).matches("^[0-9A-Za-z]{6}$");
    }

    @Test
    void generateRandomCode_shouldGenerateDifferentCodes() {
        Set<String> codes = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            codes.add(ShortCodeGenerator.generateRandomCode());
        }

        assertThat(codes).hasSize(100);
    }

    @Test
    void generateRandomCode_shouldNotBeNull() {
        String code = ShortCodeGenerator.generateRandomCode();

        assertThat(code).isNotNull();
    }

    @Test
    void generateRandomCode_shouldNotBeEmpty() {
        String code = ShortCodeGenerator.generateRandomCode();

        assertThat(code).isNotEmpty();
    }
}
