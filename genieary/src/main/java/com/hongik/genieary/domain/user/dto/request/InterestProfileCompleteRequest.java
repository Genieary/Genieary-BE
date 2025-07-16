package com.hongik.genieary.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
// 관심사 프로필 완성 요청 (2단계)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "관심사 프로필 완성 요청 (2단계)")
public class InterestProfileCompleteRequest {

    @Schema(description = "관심사 ID 목록 (카테고리 구분 없이 자유 선택)",
            example = "[1, 5, 12, 25, 30]", required = true)
    @NotNull(message = "관심사를 선택해주세요.")
    @Size(min = 1, max = 5, message = "관심사는 최소 1개, 최대 5개까지 선택할 수 있습니다.")
    private List<Long> interestIds;
}