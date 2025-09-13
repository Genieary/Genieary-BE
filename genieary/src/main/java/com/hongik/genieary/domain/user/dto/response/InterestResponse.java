package com.hongik.genieary.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hongik.genieary.domain.user.entity.Interest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class InterestResponse {
    private Long id;
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String category;

    public static InterestResponse from(Interest interest) {
        return InterestResponse.builder()
                .id(interest.getId())
                .name(interest.getName())
                .category(interest.getCategory())
                .build();
    }

    public static InterestResponse fromWithoutCategory(Interest interest) {
        return InterestResponse.builder()
                .id(interest.getId())
                .name(interest.getName())
                .build();
    }
}