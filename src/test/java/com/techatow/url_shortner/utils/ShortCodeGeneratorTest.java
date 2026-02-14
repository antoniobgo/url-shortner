package com.techatow.url_shortner.utils;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.RepeatedTest;
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
        String validChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        assertThat(code).matches("[" + validChars + "]{6}");
    }

    @RepeatedTest(100)
    void generateRandomCode_shouldGenerateDifferentCodes() {
        Set<String> codes = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            codes.add(ShortCodeGenerator.generateRandomCode());
        }

        assertThat(codes.size()).isGreaterThan(990);
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
