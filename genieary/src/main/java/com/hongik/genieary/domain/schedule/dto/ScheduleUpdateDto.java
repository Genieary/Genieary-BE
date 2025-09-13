package com.hongik.genieary.domain.schedule.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ScheduleUpdateDto {
    private String name;
    private Boolean isEvent;
    private LocalDate date;
}