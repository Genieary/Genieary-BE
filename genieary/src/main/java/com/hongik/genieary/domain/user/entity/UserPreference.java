package com.hongik.genieary.domain.user.entity;

import com.hongik.genieary.domain.common.BaseEntity;
import com.hongik.genieary.domain.preference.entity.Preference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_keyword_id")
    private Long memberKeywordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preference_id", nullable = false)
    private Preference preference;
}
