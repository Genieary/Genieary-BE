package com.hongik.genieary.domain.friendRequest.controller;

import com.hongik.genieary.auth.service.CustomUserDetails;
import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.response.ApiResponse;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.common.status.SuccessStatus;
import com.hongik.genieary.common.swagger.FriendRequestAlreadyExistsApiResponse;
import com.hongik.genieary.common.swagger.FriendRequestNotFoundApiResponse;
import com.hongik.genieary.common.swagger.FriendUserNotFoundApiResponse;
import com.hongik.genieary.domain.enums.FriendStatus;
import com.hongik.genieary.domain.friendRequest.dto.FriendRequestDto;
import com.hongik.genieary.domain.friendRequest.dto.FriendRequestStatusUpdateDto;
import com.hongik.genieary.domain.friendRequest.service.FriendRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "FriendRequest API", description = "친구 요청 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "친구 요청", description = "친구 요청을 보냅니다.")
    @FriendRequestAlreadyExistsApiResponse
    @FriendUserNotFoundApiResponse
    public ResponseEntity<ApiResponse> sendRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody FriendRequestDto requestDto) {

        friendRequestService.sendRequest(userDetails.getUser(), requestDto.getReceiverId());
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @PostMapping("/request")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "친구 요청 승인/거절", description = "친구 요청을 승인, 거절 합니다.")
    @FriendRequestNotFoundApiResponse
    public ResponseEntity<ApiResponse> updateRequestStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody FriendRequestStatusUpdateDto dto) {

        if (dto.getStatus() == FriendStatus.ACCEPTED) {
            friendRequestService.acceptRequest(userDetails.getUser(), dto.getRequestId());
        } else if (dto.getStatus() == FriendStatus.REJECTED) {
            friendRequestService.rejectRequest(userDetails.getUser(), dto.getRequestId());
        } else {
            throw new GeneralException(ErrorStatus.VALIDATION_ERROR);
        }

        return ApiResponse.onSuccess(SuccessStatus._OK);
    }
}
