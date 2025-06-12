package com.hongik.genieary.domain.friendRequest.dto;

import com.hongik.genieary.domain.enums.FriendStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FriendRequestStatusUpdateDto {
    private Long requestId;
    private FriendStatus status;
}