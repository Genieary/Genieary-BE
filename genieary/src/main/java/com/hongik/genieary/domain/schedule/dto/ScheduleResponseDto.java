package com.hongik.genieary.domain.schedule.dto;

import com.hongik.genieary.domain.schedule.entity.Schedule;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponseDto {
    private Long scheduleId;
    private String name;
    private Boolean isEvent;
    private LocalDate date;

    public static ScheduleResponseDto fromEntity(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .scheduleId(schedule.getScheduleId())
                .name(schedule.getName())
                .isEvent(schedule.getIsEvent())
                .date(schedule.getDate())
                .build();
    }
}