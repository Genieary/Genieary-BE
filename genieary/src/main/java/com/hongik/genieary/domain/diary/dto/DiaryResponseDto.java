package com.hongik.genieary.domain.diary.dto;

import lombok.*;

import java.time.LocalDate;

public class DiaryResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiaryResultDto {
        private Long diaryId;
        private String content;
        private LocalDate createdAt;
        private Boolean isLiked;
        private LocalDate diaryDate;
    }
}
