package com.hongik.genieary.domain.schedule.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.calendar.entity.Calendar;
import com.hongik.genieary.domain.calendar.repository.CalendarRepository;
import com.hongik.genieary.domain.schedule.converter.ScheduleConverter;
import com.hongik.genieary.domain.schedule.dto.ScheduleRequestDto;
import com.hongik.genieary.domain.schedule.entity.Schedule;
import com.hongik.genieary.domain.schedule.repository.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final CalendarRepository calendarRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    public void addSchedule(ScheduleRequestDto dto) {
        Calendar calendar = calendarRepository.findById(dto.getCalendarId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.SCHEDULE_CALENDAR_NOT_FOUND));

        boolean isDuplicate = scheduleRepository.existsByCalendarAndDateAndName(
                calendar, dto.getDate(), dto.getName());

        if (isDuplicate) {
            throw new GeneralException(ErrorStatus.SCHEDULE_DUPLICATED);
        }

        Schedule schedule = ScheduleConverter.toEntity(dto, calendar);
        scheduleRepository.save(schedule);
    }
}