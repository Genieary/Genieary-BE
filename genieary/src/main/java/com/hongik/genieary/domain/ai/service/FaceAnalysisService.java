package com.hongik.genieary.domain.ai.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.ai.converter.EmotionAnalysisConverter;
import com.hongik.genieary.domain.ai.dto.FastApiResponseDto;
import com.hongik.genieary.domain.ai.entity.EmotionAnalysis;
import com.hongik.genieary.domain.ai.repository.EmotionAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaceAnalysisService {

    private final EmotionAnalysisRepository emotionAnalysisRepository;
    private final EmotionAnalysisConverter emotionAnalysisConverter;

    public FastApiResponseDto.FaceAnalysisResponseDto getFaceAnalysisByDiaryId(Long diaryId) {
        EmotionAnalysis faceAnalysis = emotionAnalysisRepository.findByDiaryId(diaryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EMOTION_ANALYSIS_NOT_FOUND));

        return emotionAnalysisConverter.toDto(faceAnalysis);
    }
}
