package com.hongik.genieary.domain.schedule.repository;

import com.hongik.genieary.domain.calendar.entity.Calendar;
import com.hongik.genieary.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    boolean existsByCalendarAndDateAndName(Calendar calendar, LocalDate date, String name);
}