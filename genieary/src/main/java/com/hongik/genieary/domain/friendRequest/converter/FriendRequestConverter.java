package com.hongik.genieary.domain.friendRequest.converter;

import com.hongik.genieary.domain.enums.FriendStatus;
import com.hongik.genieary.domain.friendRequest.entity.FriendRequest;
import com.hongik.genieary.domain.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;
import com.hongik.genieary.domain.friendRequest.dto.FriendRequestResponseDto;

public class FriendRequestConverter {

    public static FriendRequest toEntity(User requester, User receiver) {
        return FriendRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendStatus.REQUESTED)
                .build();
    }

    public static FriendRequestResponseDto.FriendRequestResultDto toResponseDto(FriendRequest request) {
        User requester = request.getRequester();

        return FriendRequestResponseDto.FriendRequestResultDto.builder()
                .requestId(request.getRequestId())
                .requesterId(requester.getId())
                .nickname(requester.getNickname())
                .profileImage(requester.getProfileImg())
                .build();
    }

    public static List<FriendRequestResponseDto.FriendRequestResultDto> toResponseDtoList(List<FriendRequest> requests) {
        return requests.stream()
                .map(FriendRequestConverter::toResponseDto)
                .collect(Collectors.toList());
    }
}
