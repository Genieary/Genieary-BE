package com.hongik.genieary.domain.ai.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hongik.genieary.domain.ai.dto.FastApiResponseDto;
import com.hongik.genieary.domain.ai.entity.EmotionAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmotionAnalysisConverter {

    private final ObjectMapper objectMapper;

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

    public FastApiResponseDto.FaceAnalysisResponseDto toDto(EmotionAnalysis entity) {
        return FastApiResponseDto.FaceAnalysisResponseDto.builder()
                .predictedEmotion(entity.getPredictedEmotion())
                .allPredictions(convertJsonToMap(entity.getAllPredictions()))
                .analysis(entity.getAnalysis())
                .build();
    }

    private Map<String, String> convertJsonToMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to map", e);
        }
    }
}
