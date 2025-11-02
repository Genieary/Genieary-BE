package com.hongik.genieary.domain.ai.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.ai.dto.FastApiResponseDto;
import com.hongik.genieary.domain.diary.entity.Diary;
import com.hongik.genieary.domain.diary.repository.DiaryRepository;
import com.hongik.genieary.infra.fastapi.FastApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FastApiService {

    private final FastApiClient fastApiClient;
    private final OpenAiService openAiService;
    private final DiaryRepository diaryRepository;
    private final EmotionAnalysisRepository emotionAnalysisRepository;

    public FastApiResponseDto.FaceAnalysisResponseDto analyzeFace(Long userId, LocalDate diaryDate, String faceImg) {
        Long diaryId = diaryRepository.findDiaryIdByUserIdAndDiaryDate(userId, diaryDate)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));

        FastApiResponseDto.FaceEmotionResponseDto dto = fastApiClient.analyzeFace(faceImg);

        String diaryContent = diaryRepository.findByUserIdAndDiaryDate(userId, diaryDate)
                .map(Diary::getContent)
                .orElse(null);

        FastApiResponseDto.FaceAnalysisResponseDto result = openAiService.getFaceAnalysis(faceImg, dto.getPredictedEmotion(), dto.getAllPredictions(), diaryContent);

        // 감정분석 있으면 삭제
        emotionAnalysisRepository.findByDiaryId(diaryId).ifPresent(emotionAnalysisRepository::delete);

        return result;
    }
}
