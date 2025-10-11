package com.hongik.genieary.domain.calendar.service;

import com.hongik.genieary.domain.calendar.dto.CalendarResponseDto;

public interface CalendarService {
    String getSummary(Long userId,  Long calendarId);
    CalendarResponseDto.CalendarResultDto getCalendar(Long userId, int year, int month);
}
