package com.hongik.genieary.domain.user.entity;

import com.hongik.genieary.domain.common.BaseEntity;
import com.hongik.genieary.domain.enums.Gender;
import com.hongik.genieary.domain.enums.LoginType;
import com.hongik.genieary.domain.enums.Personality;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_file_name", length = 2083)
    private String imageFileName;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;
  
    // 성격을 여러 개 저장하기 위한 컬렉션
    @ElementCollection(targetClass = Personality.class)
    @CollectionTable(name = "user_personalities", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "personality")
    @Builder.Default
    private Set<Personality> personalities = new HashSet<>();

    @Column(name = "basic_profile_completed", nullable = false)
    @Builder.Default
    private Boolean basicProfileCompleted = false;

    @Column(name = "interest_profile_completed", nullable = false)
    @Builder.Default
    private Boolean interestProfileCompleted = false;

    // ----- method ----
    // 프로필 업데이트 메서드
    public void updateProfile(String nickname, LocalDate birthDate, Gender gender, Set<Personality> personalities) {
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.gender = gender;
        this.personalities = personalities != null ? new HashSet<>(personalities) : new HashSet<>();
        this.basicProfileCompleted = true;
    }

    // 관심사 프로필 완성
    public void completeInterestProfile() {
        this.interestProfileCompleted = true;
    }

    // 전체 프로필 완성 여부 확인
    public boolean isProfileCompleted() {
        return this.basicProfileCompleted && this.interestProfileCompleted;
    }

    // 프로필 사진 업데이트 메서드
    public void updateImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }


}
