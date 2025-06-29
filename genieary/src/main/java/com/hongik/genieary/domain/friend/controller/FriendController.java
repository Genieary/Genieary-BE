package com.hongik.genieary.domain.friend.controller;

import com.hongik.genieary.auth.service.CustomUserDetails;
import com.hongik.genieary.common.response.ApiResponse;
import com.hongik.genieary.common.status.SuccessStatus;
import com.hongik.genieary.common.swagger.FriendNotFoundApiResponse;
import com.hongik.genieary.common.swagger.FriendSearchSuccessApiResponse;
import com.hongik.genieary.common.swagger.FriendUserNotFoundApiResponse;
import com.hongik.genieary.common.swagger.InvalidSearchKeywordApiResponse;
import com.hongik.genieary.domain.friend.dto.FriendResponseDto;
import com.hongik.genieary.domain.friend.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    @FriendNotFoundApiResponse
    @FriendUserNotFoundApiResponse
    public ResponseEntity<ApiResponse> deleteFriend(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long friendId) {

        friendService.deleteFriend(userDetails.getUser(), friendId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @GetMapping("/{friendId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "친구 프로필", description = "친구 ID에 해당하는 친구의 프로필을 조회합니다.")
    @FriendNotFoundApiResponse
    public ResponseEntity<ApiResponse> getFriendProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long friendId) {

        FriendResponseDto.FriendProfileDto profile = friendService.getFriendProfile(userDetails.getUser(), friendId);
        return ApiResponse.onSuccess(SuccessStatus._OK, profile);
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "친구 검색", description = "닉네임에 해당하는 유저를 검색합니다.")
    @FriendSearchSuccessApiResponse
    @InvalidSearchKeywordApiResponse
    public ResponseEntity<ApiResponse> searchFriends(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String nickname,
            @ParameterObject @PageableDefault(size = 10, sort = "nickname", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<FriendResponseDto.FriendSearchResultDto> resultPage = friendService.searchFriends(userDetails.getUser(),nickname, pageable);

        return ApiResponse.onSuccess(SuccessStatus._OK, resultPage);
    }
}