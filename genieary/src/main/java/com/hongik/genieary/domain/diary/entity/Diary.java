package com.hongik.genieary.domain.diary.entity;

import com.hongik.genieary.domain.calendar.entity.Calendar;
import com.hongik.genieary.domain.common.BaseEntity;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.ai.entity.EmotionAnalysis;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


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

    @Column(name = "diaryDate")
    private LocalDate diaryDate;

    @Column(name = "imageFileName")
    private String imageFileName;

    @OneToOne(mappedBy = "diary", cascade = CascadeType.ALL)
    private EmotionAnalysis emotionAnalysis;

    public void update(String content, Boolean isLiked) {
        if (content != null) this.content = content;
        if (isLiked != null) this.isLiked = isLiked;
    }

    public void uploadImageFileName(String fileName) {
        this.imageFileName = fileName;
    }
}