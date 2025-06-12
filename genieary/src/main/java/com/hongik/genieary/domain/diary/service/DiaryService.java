package com.hongik.genieary.domain.diary.service;

import com.hongik.genieary.auth.service.CustomUserDetails;
import com.hongik.genieary.domain.diary.dto.DiaryRequestDto;
import com.hongik.genieary.domain.diary.dto.DiaryResponseDto;
import com.hongik.genieary.domain.user.entity.User;

public interface DiaryService {

    DiaryResponseDto.DiaryResultDto createDiary(CustomUserDetails user, DiaryRequestDto.CreateDto requestDto);
    DiaryResponseDto.DiaryResultDto updateDiary(Long diaryId, User user, DiaryRequestDto.UpdateDto dto);
}
