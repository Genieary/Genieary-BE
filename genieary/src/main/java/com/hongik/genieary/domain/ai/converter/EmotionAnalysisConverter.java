package com.hongik.genieary.domain.ai.converter;

import com.hongik.genieary.domain.ai.dto.FastApiResponseDto;
import com.hongik.genieary.domain.ai.entity.EmotionAnalysis;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmotionAnalysisConverter {

    public EmotionAnalysis toEntity(FastApiResponseDto.FaceAnalysisResponseDto dto, Long diaryId) {
        return EmotionAnalysis.builder()
                .predictedEmotion(dto.getPredictedEmotion())
                .allPredictions(convertMapToJson(dto.getAllPredictions()))
                .analysis(dto.getAnalysis())
                .diaryId(diaryId)
                .build();
    }

    private String convertMapToJson(Map<String, String> map) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert map to JSON", e);
        }
    }
}
