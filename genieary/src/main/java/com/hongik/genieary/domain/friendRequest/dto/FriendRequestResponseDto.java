package com.hongik.genieary.domain.friendRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FriendRequestResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendRequestResultDto {
        private Long requestId;
        private Long requesterId;
        private String nickname;
        private String profileImage;
    }
}