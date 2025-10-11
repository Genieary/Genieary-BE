package com.hongik.genieary.domain.friendRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SentFriendRequestResultDto {
        private Long requestId;
        private Long receiverId;
        private String nickname;
        private String profileImage;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendRequestBoxDto {
        private List<FriendRequestResultDto> received;              // 내가 받은(요청자 정보)
        private List<SentFriendRequestResultDto> sent;              // 내가 보낸(수신자 정보)
    }
}