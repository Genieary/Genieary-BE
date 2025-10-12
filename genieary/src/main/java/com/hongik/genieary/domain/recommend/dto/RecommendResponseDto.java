package com.hongik.genieary.domain.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RecommendResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GiftResultDto {
        private String name;
        private String description;
        private String imageUrl;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GiftImageResultDto {
        private String name;
        private String imageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeResultDto {
        private Long recommendId;
        private Boolean isLiked;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HateResultDto {
        private Long recommendId;
        private Boolean isHated;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VisibilityResultDto {
        private Long recommendId;
        private boolean isPublic;
    }
}