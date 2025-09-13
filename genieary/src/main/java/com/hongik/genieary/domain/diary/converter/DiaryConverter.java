package com.hongik.genieary.domain.diary.converter;


import com.hongik.genieary.domain.calendar.entity.Calendar;
import com.hongik.genieary.domain.diary.dto.DiaryRequestDto;
import com.hongik.genieary.domain.diary.dto.DiaryResponseDto;
import com.hongik.genieary.domain.diary.entity.Diary;
import com.hongik.genieary.domain.user.entity.User;

public class DiaryConverter {

    public static Diary toEntity(User user, Calendar calendar, DiaryRequestDto.DiaryCreateDto dto) {
        return Diary.builder()
                .user(user)
                .calendar(calendar)
                .content(dto.getContent())
                .isLiked(dto.getIsLiked() != null ? dto.getIsLiked() : false)
                .diaryDate(dto.getDiaryDate())
                .build();
    }

    public static DiaryResponseDto.DiaryResultDto toResponseDto(Diary diary) {
        return DiaryResponseDto.DiaryResultDto.builder()
                .diaryId(diary.getDiaryId())
                .content(diary.getContent())
                .createdAt(diary.getCreatedAt())
                .isLiked(diary.getIsLiked())
                .diaryDate(diary.getDiaryDate())
                .build();
    }

    public static DiaryResponseDto.DiaryFaceImageResultDto toFaceImageResponseDto(Long diaryId, String url) {
        return DiaryResponseDto.DiaryFaceImageResultDto.builder()
                .diaryId(diaryId)
                .url(url)
                .build();
    }

}
