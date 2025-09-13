package com.hongik.genieary.domain.schedule.entity;

import com.hongik.genieary.domain.calendar.entity.Calendar;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
@Table(name = "schedule")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;

    @Column(nullable = false, length = 15)
    private String name;

    @Column(nullable = false)
    private Boolean isEvent;

    @Column(nullable = false)
    private LocalDate date;

    public void update(String name, Boolean isEvent, LocalDate date, Calendar calendar) {
        if (name != null) this.name = name;
        if (isEvent != null) this.isEvent = isEvent;
        if (date != null) this.date = date;
        if (calendar != null) this.calendar = calendar;
    }
}