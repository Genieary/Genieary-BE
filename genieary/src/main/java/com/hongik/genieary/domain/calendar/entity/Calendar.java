package com.hongik.genieary.domain.calendar.entity;

import com.hongik.genieary.domain.common.BaseEntity;
import com.hongik.genieary.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

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
}
