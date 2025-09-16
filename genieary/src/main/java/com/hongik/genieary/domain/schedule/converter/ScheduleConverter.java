package com.hongik.genieary.domain.schedule.converter;

import com.hongik.genieary.domain.calendar.entity.Calendar;
import com.hongik.genieary.domain.schedule.dto.ScheduleRequestDto;
import com.hongik.genieary.domain.schedule.dto.ScheduleResponseDto;
import com.hongik.genieary.domain.schedule.entity.Schedule;

public class ScheduleConverter {

    public static Schedule toEntity(ScheduleRequestDto dto, Calendar calendar) {
        return Schedule.builder()
                .calendar(calendar)
                .name(dto.getName())
                .isEvent(dto.getIsEvent())
                .date(dto.getDate())
                .build();
    }

    public static ScheduleResponseDto toDto(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .scheduleId(schedule.getScheduleId())
                .name(schedule.getName())
                .isEvent(schedule.getIsEvent())
                .date(schedule.getDate())
                .build();
    }
}