package com.hongik.genieary.domain.diary.service;

import com.hongik.genieary.auth.service.CustomUserDetails;
import com.hongik.genieary.domain.diary.dto.DiaryRequestDto;
import com.hongik.genieary.domain.diary.dto.DiaryResponseDto;

import java.time.LocalDate;

public interface DiaryService {

    DiaryResponseDto.DiaryResultDto createDiary(CustomUserDetails user, DiaryRequestDto.DiaryCreateDto requestDto);
    DiaryResponseDto.DiaryResultDto updateDiary(Long diaryId, Long userId, DiaryRequestDto.DiaryUpdateDto dto);
    void deleteDiary(Long userId, Long diaryId);
    DiaryResponseDto.DiaryResultDto getDiary(Long diaryId, Long userId);
    DiaryResponseDto.DiaryFaceImageResultDto uploadDiaryFaceImage(Long userId, LocalDate date, String contentType);
    DiaryResponseDto.DiaryFaceImageResultDto getDiaryFaceImageUrl(Long userId, Long diaryId);
}
