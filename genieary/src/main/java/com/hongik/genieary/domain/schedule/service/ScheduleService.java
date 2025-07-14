package com.hongik.genieary.domain.schedule.service;

import com.hongik.genieary.domain.schedule.dto.ScheduleRequestDto;
import com.hongik.genieary.domain.schedule.dto.ScheduleResponseDto;
import com.hongik.genieary.domain.schedule.dto.ScheduleUpdateDto;
import com.hongik.genieary.domain.user.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    void addSchedule(ScheduleRequestDto dto);
    List<ScheduleResponseDto> getSchedules(User user, LocalDate date);
    void deleteSchedule(User user, Long scheduleId);
    void updateSchedule(User user, Long scheduleId, ScheduleUpdateDto dto);
}