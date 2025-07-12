package com.hongik.genieary.domain.schedule.dto;

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
}