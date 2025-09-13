package com.hongik.genieary.domain.schedule.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequestDto {
    private Long calendarId;
    private String name;
    private Boolean isEvent;
    private LocalDate date;
}
