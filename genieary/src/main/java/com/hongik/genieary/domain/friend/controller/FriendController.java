package com.hongik.genieary.domain.friend.controller;

import com.hongik.genieary.auth.service.CustomUserDetails;
import com.hongik.genieary.common.response.ApiResponse;
import com.hongik.genieary.common.status.SuccessStatus;
import com.hongik.genieary.domain.friend.dto.FriendResponseDto;
import com.hongik.genieary.domain.friend.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Friend API", description = "친구 CRUD API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "친구 목록 조회", description = "친구 목록을 조회합니다.")
    public ResponseEntity<ApiResponse> getFriendList(@AuthenticationPrincipal CustomUserDetails userDetails) {

        List<FriendResponseDto.FriendListResultDto> friends = friendService.getFriendList(userDetails.getUser());
        return ApiResponse.onSuccess(SuccessStatus._OK, friends);
    }

    @DeleteMapping("/{friendId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "친구 삭제", description = "친구 ID에 해당하는 친구를 삭제합니다.")
    public ResponseEntity<ApiResponse> deleteFriend(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long friendId) {

        friendService.deleteFriend(userDetails.getUser(), friendId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }
}