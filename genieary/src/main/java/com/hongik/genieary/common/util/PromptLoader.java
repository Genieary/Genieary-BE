package com.hongik.genieary.common.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class PromptLoader {

    public String loadPrompt(String fileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("prompts/" + fileName)) {
            if (is == null) {
                throw new IllegalArgumentException("프롬프트 파일을 찾을 수 없습니다: " + fileName);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("프롬프트 파일 읽기 실패", e);
        }
    }
}