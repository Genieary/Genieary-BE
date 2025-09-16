package com.hongik.genieary.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 관심사 응답")
public class UserInterestResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "관심사 목록")
    private List<InterestResponse> interests;

    @Schema(description = "관심사 개수", example = "3")
    private Integer interestCount;

    public static UserInterestResponse from(Long userId, List<InterestResponse> interests) {
        return UserInterestResponse.builder()
                .userId(userId)
                .interests(interests)
                .interestCount(interests.size())
                .build();
    }
}