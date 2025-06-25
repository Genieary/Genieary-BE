package com.hongik.genieary.domain.user.controller;

import com.hongik.genieary.auth.service.CustomUserDetails;
import com.hongik.genieary.common.response.ApiResponse;
import com.hongik.genieary.common.status.SuccessStatus;
import com.hongik.genieary.domain.user.dto.request.ProfileCompleteRequest;
import com.hongik.genieary.domain.user.dto.request.ProfileUpdateRequest;
import com.hongik.genieary.domain.user.dto.response.ProfileResponse;
import com.hongik.genieary.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    // 프로필 완성 (첫 로그인 시)
    @PostMapping("/profile")
    public ResponseEntity<ApiResponse> completeProfile(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody ProfileCompleteRequest request) {

        ProfileResponse response = userService.completeProfile(userId, request);
        return ApiResponse.onSuccess(SuccessStatus.PROFILE_COMPLETED, response);
    }

    // 프로필 수정
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateProfile(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody ProfileUpdateRequest request) {

        ProfileResponse response = userService.updateProfile(userId, request);
        return ApiResponse.onSuccess(SuccessStatus.PROFILE_UPDATED, response);
    }

    // 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(
            @AuthenticationPrincipal(expression = "id") Long userId) {

        ProfileResponse response = userService.getProfile(userId);
        return ApiResponse.onSuccess(SuccessStatus.PROFILE_RETRIEVED, response);
    }

    // 프로필 완성 여부 확인
    @GetMapping("/profile/status")
    public ResponseEntity<ApiResponse> getProfileStatus(
            @AuthenticationPrincipal(expression = "id") Long userId) {

        boolean isCompleted = userService.isProfileCompleted(userId);
        return ApiResponse.onSuccess(SuccessStatus.PROFILE_STATUS_RETRIEVED, isCompleted);
    }
}
