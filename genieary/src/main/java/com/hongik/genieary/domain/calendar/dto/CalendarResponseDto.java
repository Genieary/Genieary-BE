package com.hongik.genieary.domain.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CalendarResponseDto {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalendarResultDto {
        private Long calendarId;
        private int year;
        private int month;
    }
}
