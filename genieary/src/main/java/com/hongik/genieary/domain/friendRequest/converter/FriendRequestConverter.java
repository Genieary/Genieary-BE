package com.hongik.genieary.domain.friendRequest.converter;

import com.hongik.genieary.domain.enums.FriendStatus;
import com.hongik.genieary.domain.friendRequest.entity.FriendRequest;
import com.hongik.genieary.domain.user.entity.User;

import java.util.List;
import java.util.Map;
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

    public static FriendRequestResponseDto.FriendRequestResultDto toResponseDto(FriendRequest request, String presignedUrl) {
        User requester = request.getRequester();

        return FriendRequestResponseDto.FriendRequestResultDto.builder()
                .requestId(request.getRequestId())
                .requesterId(requester.getId())
                .nickname(requester.getNickname())
                .profileImage(presignedUrl)
                .build();
    }

    public static List<FriendRequestResponseDto.FriendRequestResultDto> toResponseDtoList(List<FriendRequest> requests, Map<Long, String> userIdToUrlMap) {
        return requests.stream()
                .map(request -> {
                    User requester = request.getRequester();
                    String url = userIdToUrlMap.get(requester.getId());
                    return toResponseDto(request, url);
                })
                .collect(Collectors.toList());
    }
}
