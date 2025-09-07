package com.hongik.genieary.domain.calendar.entity;

import com.hongik.genieary.domain.common.BaseEntity;
import com.hongik.genieary.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "calendar")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Calendar extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long calendarId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "summary", length=500)
    private String summary;

    @Column(name = "year", length=4)
    private int year;

    @Column(name = "month", length=2)
    private int month;

    private LocalDateTime modifiedAt;

    public static Calendar of(User user, int year, int month) {
        System.out.println(month+"이게 뭐냐능!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        return Calendar.builder()
                .user(user)
                .year(year)
                .month(month)
                .build();
    }

    public void updateModifiedAt() {
        this.modifiedAt = LocalDateTime.now();
    }

    public void updateSummary(String summary) {
        this.summary = summary;
    }

    public void clearSummary() {
        this.summary = null;
    }

}
