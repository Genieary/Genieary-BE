package com.hongik.genieary.domain.ai.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.ai.converter.EmotionAnalysisConverter;
import com.hongik.genieary.domain.ai.dto.FastApiResponseDto;
import com.hongik.genieary.domain.ai.entity.EmotionAnalysis;
import com.hongik.genieary.domain.ai.repository.EmotionAnalysisRepository;
import com.hongik.genieary.domain.diary.entity.Diary;
import com.hongik.genieary.domain.diary.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaceAnalysisService {

    private final EmotionAnalysisRepository emotionAnalysisRepository;
    private final EmotionAnalysisConverter emotionAnalysisConverter;
    private final DiaryRepository diaryRepository;

    public FastApiResponseDto.FaceAnalysisResponseDto getFaceAnalysisByDiaryId(Long diaryId, Long userId) {

        diaryRepository.findByDiaryIdAndUserId(diaryId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));

        EmotionAnalysis faceAnalysis = emotionAnalysisRepository.findByDiaryId(diaryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EMOTION_ANALYSIS_NOT_FOUND));

        return emotionAnalysisConverter.toDto(faceAnalysis);
    }

    @Transactional
    public void deleteFaceAnalysis(Long diaryId, Long userId) {

        diaryRepository.findByDiaryIdAndUserId(diaryId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));

        EmotionAnalysis faceAnalysis = emotionAnalysisRepository.findByDiaryId(diaryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EMOTION_ANALYSIS_NOT_FOUND));

        emotionAnalysisRepository.delete(faceAnalysis);
    }
}
