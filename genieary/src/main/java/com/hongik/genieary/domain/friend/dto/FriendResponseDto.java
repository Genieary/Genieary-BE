package com.hongik.genieary.domain.friend.dto;

import com.hongik.genieary.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
