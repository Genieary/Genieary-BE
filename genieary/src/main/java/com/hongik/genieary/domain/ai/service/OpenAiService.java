package com.hongik.genieary.domain.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.common.util.PromptLoader;
import com.hongik.genieary.domain.ai.dto.FastApiResponseDto;
import com.hongik.genieary.domain.recommend.Category;
import com.hongik.genieary.domain.recommend.dto.RecommendResponseDto;
import com.hongik.genieary.infra.openai.OpenAiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final PromptLoader promptLoader;
    private final OpenAiClient openAiClient; // RestTemplate이나 WebClient로 감싼 API 호출기

    public String summarizeMonthlyDiary(String name, String year, String month, String diaryText) {
        String template = promptLoader.loadPrompt("monthly_summary_prompt.txt");

        String prompt = template
                .replace("{name}", name)
                .replace("{year}", year)
                .replace("{month}", month)
                .replace("{diary}", diaryText);

        return openAiClient.requestChatCompletion(prompt);
    }

    public List<RecommendResponseDto.GiftRecommendResultDto> getRecommendations(String personalities, String interests, Category category, String eventText) {
        String template = promptLoader.loadPrompt("recommend_gifts_prompt.txt");

        String prompt = template
                .replace("{personalities}", personalities)
                .replace("{interests}", interests)
                .replace("{category}", category.name())
                .replace("{event}", eventText);;

        String response = openAiClient.requestChatCompletion(prompt);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(
                    response,
                    new TypeReference<List<RecommendResponseDto.GiftRecommendResultDto>>() {}
            );
        } catch (JsonProcessingException e) {
            throw new GeneralException(ErrorStatus.JSON_PARSE_ERROR);
        }

    }

    public FastApiResponseDto.FaceAnalysisResponseDto getFaceAnalysis(String faceImg, String predictedEmotion, Map<String, String> allPredictions, String diaryContent) {

        String template = promptLoader.loadPrompt("analyze_face_prompt.txt");

        String readablePredictions = allPredictions.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue() + "%")
                .collect(Collectors.joining(", "));

        if (diaryContent == null || diaryContent.isBlank()) {
            diaryContent = "No diary content was provided. Please analyze only based on the emotion data.";
        }

        String prompt = template
                .replace("{faceImgUrl}", faceImg)
                .replace("{predictedEmotion}", predictedEmotion)
                .replace("{allPredictions}", readablePredictions)
                .replace("{diaryContent}", diaryContent);

        String response = openAiClient.requestChatCompletion(prompt);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(
                    response,
                    new TypeReference<FastApiResponseDto.FaceAnalysisResponseDto>() {}
            );
        } catch (JsonProcessingException e) {
            throw new GeneralException(ErrorStatus.JSON_PARSE_ERROR);
        }
    }
}
