package com.hongik.genieary.domain.diary.entity;

import com.hongik.genieary.domain.calendar.entity.Calendar;
import com.hongik.genieary.domain.common.BaseEntity;
import com.hongik.genieary.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Diary extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    @Column(name = "content", length = 1500)
    private String content;

    @Column(name = "isLiked")
    private Boolean isLiked;



}