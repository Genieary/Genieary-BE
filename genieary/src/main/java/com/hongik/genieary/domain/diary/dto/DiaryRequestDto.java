package com.hongik.genieary.domain.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DiaryRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDto {
        private String content;
        private Boolean isLiked;
    }
}
