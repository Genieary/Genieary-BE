package com.hongik.genieary.domain.schedule.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.calendar.entity.Calendar;
import com.hongik.genieary.domain.calendar.repository.CalendarRepository;
import com.hongik.genieary.domain.schedule.converter.ScheduleConverter;
import com.hongik.genieary.domain.schedule.dto.ScheduleRequestDto;
import com.hongik.genieary.domain.schedule.dto.ScheduleResponseDto;
import com.hongik.genieary.domain.schedule.entity.Schedule;
import com.hongik.genieary.domain.schedule.repository.ScheduleRepository;
import com.hongik.genieary.domain.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<ScheduleResponseDto> getSchedules(User user, LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        Calendar calendar = calendarRepository.findByUserAndYearAndMonth(user, year, month)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SCHEDULE_CALENDAR_NOT_FOUND));

        List<Schedule> schedules = scheduleRepository.findByCalendarAndDate(calendar, date);
        return schedules.stream()
                .map(ScheduleResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSchedule(User user, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SCHEDULE_NOT_FOUND));

        if (!schedule.getCalendar().getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus.SCHEDULE_ACCESS_FORBIDDEN);
        }

        scheduleRepository.delete(schedule);
    }
}