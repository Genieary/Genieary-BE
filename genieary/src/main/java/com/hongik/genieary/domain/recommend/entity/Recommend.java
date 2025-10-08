package com.hongik.genieary.domain.recommend.entity;

import com.hongik.genieary.domain.common.BaseEntity;
import com.hongik.genieary.domain.diary.entity.Diary;
import com.hongik.genieary.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Recommend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommendId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    private String contentName;

    private String contentImage;

    private String contentDescription;

    private boolean isLiked;

    private boolean isHated;

    public boolean togleLike() {
        this.isLiked = !this.isLiked;
        return this.isLiked;
    }

    public boolean togleHate() {
        this.isHated = !this.isHated;
        return this.isHated;
    }
}