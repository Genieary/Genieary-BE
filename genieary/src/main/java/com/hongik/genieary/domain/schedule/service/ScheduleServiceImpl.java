package com.hongik.genieary.domain.schedule.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.calendar.entity.Calendar;
import com.hongik.genieary.domain.calendar.repository.CalendarRepository;
import com.hongik.genieary.domain.schedule.converter.ScheduleConverter;
import com.hongik.genieary.domain.schedule.dto.ScheduleRequestDto;
import com.hongik.genieary.domain.schedule.dto.ScheduleResponseDto;
import com.hongik.genieary.domain.schedule.dto.ScheduleUpdateDto;
import com.hongik.genieary.domain.schedule.entity.Schedule;
import com.hongik.genieary.domain.schedule.repository.ScheduleRepository;
import com.hongik.genieary.domain.user.entity.User;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleServiceImpl implements ScheduleService {

    private final CalendarRepository calendarRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    @Override
    public ScheduleResponseDto addSchedule(User user, ScheduleRequestDto dto) {
        LocalDate date = dto.getDate();
        int year = date.getYear();
        int month = date.getMonthValue();

        Calendar calendar;

        if (dto.getCalendarId() != null) {
            calendar = calendarRepository.findById(dto.getCalendarId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.SCHEDULE_CALENDAR_NOT_FOUND));

            if (calendar.getYear() != year || calendar.getMonth() != month) {
                throw new GeneralException(ErrorStatus.SCHEDULE_INVALID_CALENDAR_DATE_MISMATCH);
            }
        } else {
            calendar = calendarRepository.findByUserAndYearAndMonth(user, year, month)
                    .orElseGet(() -> calendarRepository.save(Calendar.of(user, year, month)));
        }

        boolean isDuplicate = scheduleRepository.existsByCalendarAndDateAndName(calendar, date, dto.getName());
        if (isDuplicate) {
            throw new GeneralException(ErrorStatus.SCHEDULE_DUPLICATED);
        }

        Schedule schedule = ScheduleConverter.toEntity(dto, calendar);
        Schedule saved = scheduleRepository.save(schedule);
        return ScheduleResponseDto.fromEntity(saved);
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

    @Transactional
    @Override
    public void deleteSchedule(User user, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SCHEDULE_NOT_FOUND));

        if (!schedule.getCalendar().getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus.SCHEDULE_ACCESS_FORBIDDEN);
        }

        scheduleRepository.delete(schedule);
    }

    @Transactional
    @Override
    public void updateSchedule(User user, Long scheduleId, ScheduleUpdateDto dto) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SCHEDULE_NOT_FOUND));

        if (!schedule.getCalendar().getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus.SCHEDULE_ACCESS_FORBIDDEN);
        }

        if (dto.getName() == null && dto.getIsEvent() == null && dto.getDate() == null) {
            throw new GeneralException(ErrorStatus.SCHEDULE_INVALID_REQUEST);
        }

        String updatedName = dto.getName() != null ? dto.getName() : schedule.getName();
        LocalDate updatedDate = dto.getDate() != null ? dto.getDate() : schedule.getDate();
        Calendar currentCalendar = schedule.getCalendar();
        Calendar newCalendar = currentCalendar;

        if (dto.getDate() != null) {
            int newYear = updatedDate.getYear();
            int newMonth = updatedDate.getMonthValue();

            if (currentCalendar.getYear() != newYear || currentCalendar.getMonth() != newMonth) {
                newCalendar = calendarRepository.findByUserAndYearAndMonth(user, newYear, newMonth)
                        .orElseGet(() -> calendarRepository.save(Calendar.of(user, newYear, newMonth)));
            }
        }

        boolean isDuplicate = scheduleRepository.existsByCalendarAndDateAndNameAndScheduleIdNot(
                newCalendar, updatedDate, updatedName, scheduleId);
        if (isDuplicate) {
            throw new GeneralException(ErrorStatus.SCHEDULE_DUPLICATED);
        }

        schedule.update(dto.getName(), dto.getIsEvent(), dto.getDate(), newCalendar);
    }

    @Override
    public List<ScheduleResponseDto> getMonthlyEvents(User user, int year, int month) {
        if (month < 1 || month > 12) {
            throw new GeneralException(ErrorStatus.SCHEDULE_INVALID_REQUEST);
        }

        Calendar calendar = calendarRepository.findByUserAndYearAndMonth(user, year, month)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SCHEDULE_CALENDAR_NOT_FOUND));

        List<Schedule> events = scheduleRepository.findByCalendarAndIsEventTrue(calendar);
        return events.stream()
                .map(ScheduleResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}