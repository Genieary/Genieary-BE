package com.hongik.genieary.domain.user.dto.request;

import com.hongik.genieary.domain.enums.Gender;
import com.hongik.genieary.domain.enums.Personality;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
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
public class ProfileUpdateRequest {

    @Size(max = 50, message = "닉네임은 50자 이하여야 합니다.")
    private String nickname;

    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birthDate;

    private Gender gender;

    @Size(min = 1, max = 3, message = "성격은 최소 1개, 최대 3개까지 선택할 수 있습니다.")
    private Set<Personality> personalities;

}