package com.hongik.genieary.domain.friend.dto;

import com.hongik.genieary.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FriendResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendListResultDto{
        private Long friendId;
        private String nickname;
        private String profileImage;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendProfileDto {
        private Long friendId;
        private String nickname;
        private String email;
        private String profileImage;
        private List<GiftPreviewDto> giftLikes;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class GiftPreviewDto {
            private Long giftId;
            private String name;
            private String imageUrl;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendSearchResultDto {
        private Long friendId;
        private String nickname;
        private String profileImage;
        private String email;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendItem {
        private Long userId;
        private String nickname;
        private String profileImage;            // presigned URL
        private Integer totalOverlap;       // 성격+관심사 겹침 수
        private Integer personalityOverlap; // 성격 겹침 수
        private Integer interestOverlap;    // 관심사 겹침 수
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendGiftDto {
        private Long recommendId;
        private String name;
        private String imageUrl;
        private String description;
        private LocalDate updatedAt;
    }
}
