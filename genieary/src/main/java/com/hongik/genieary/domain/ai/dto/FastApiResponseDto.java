package com.hongik.genieary.domain.ai.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

public class FastApiResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class FaceEmotionResponseDto {
        private String predictedEmotion;
        private Map<String, String> allPredictions;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FaceAnalysisResponseDto {
        private String predictedEmotion;
        private Map<String, String> allPredictions;
        private String analysis;
    }
}
