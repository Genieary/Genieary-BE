package com.hongik.genieary.domain.user.dto.response;

import com.hongik.genieary.domain.enums.Gender;
import com.hongik.genieary.domain.enums.Personality;
import com.hongik.genieary.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {
    private Long id;
    private String profileImage;
    private String nickname;
    private String email;
    private LocalDate birthDate;
    private Gender gender;
    private Set<Personality> personalities;

    @Schema(description = "기본 프로필 완성 여부", example = "true")
    private Boolean basicProfileCompleted;

    @Schema(description = "관심사 프로필 완성 여부", example = "true")
    private Boolean interestProfileCompleted;

    @Schema(description = "전체 프로필 완성 여부", example = "true")
    private Boolean isProfileCompleted;

    public static ProfileResponse from(User user) {
        return from(user, user.getImageFileName());
    }

    public static ProfileResponse from(User user, String imageUrl) {
        return ProfileResponse.builder()
                .id(user.getId())
                .profileImage(imageUrl)
                .nickname(user.getNickname())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .personalities(user.getPersonalities())
                .basicProfileCompleted(user.getBasicProfileCompleted())
                .interestProfileCompleted(user.getInterestProfileCompleted())
                .isProfileCompleted(user.isProfileCompleted())
                .build();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ProfilePresignedUrlResponse {
        private String url;
    }
}