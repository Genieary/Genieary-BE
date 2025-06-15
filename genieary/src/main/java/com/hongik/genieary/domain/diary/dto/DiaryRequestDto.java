package com.hongik.genieary.domain.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class DiaryRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiaryCreateDto {
        private String content;
        private Boolean isLiked;
        private LocalDate diaryDate;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiaryUpdateDto {
        private String content;
        private Boolean isLiked;
    }
}
