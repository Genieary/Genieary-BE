package com.hongik.genieary.domain.user.controller;

import com.hongik.genieary.common.response.ApiResponse;
import com.hongik.genieary.common.status.SuccessStatus;
import com.hongik.genieary.domain.user.dto.request.InterestProfileCompleteRequest;
import com.hongik.genieary.domain.user.dto.request.ProfileCompleteRequest;
import com.hongik.genieary.domain.user.dto.request.ProfileUpdateRequest;
import com.hongik.genieary.domain.user.dto.response.InterestResponse;
import com.hongik.genieary.domain.user.dto.response.ProfileResponse;
import com.hongik.genieary.domain.user.dto.response.UserInterestResponse;
import com.hongik.genieary.domain.user.service.InterestService;
import com.hongik.genieary.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Profile", description = "사용자 프로필 관리 API")
public class UserController {

    private final UserService userService;
    private final InterestService interestService;

    // 프로필 완성 (첫 로그인 시)
    @Operation(
            summary = "프로필 등록(1단계)",
            description = "첫 로그인 시 사용자 프로필을 완성합니다. 닉네임, 생년월일, 성별, 성격(1-3개)을 입력받습니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로필 완성 성공",
                            content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            }
    )
    @PostMapping("/profile")
    public ResponseEntity<ApiResponse> completeProfile(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody ProfileCompleteRequest request) {

        ProfileResponse response = userService.completeProfile(userId, request);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    // 프로필 수정
    @Operation(
            summary = "프로필 수정",
            description = "사용자 프로필을 부분 수정합니다. 변경하고 싶은 필드만 포함하세요. null 필드는 변경되지 않습니다.")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateProfile(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody ProfileUpdateRequest request) {

        ProfileResponse response = userService.updateProfile(userId, request);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    // 프로필 조회
    @Operation(
            summary = "프로필 조회",
            description = "현재 로그인한 사용자의 프로필 정보를 조회합니다.")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(
            @AuthenticationPrincipal(expression = "id") Long userId) {

        ProfileResponse response = userService.getProfile(userId);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    // 프로필 완성 여부 확인
    @Operation(
            summary = "프로필 완성 여부 확인",
            description = "사용자의 프로필 완성 여부를 확인합니다. 첫 로그인 사용자 판별에 사용됩니다.")
    @GetMapping("/profile/status")
    public ResponseEntity<ApiResponse> getProfileStatus(
            @AuthenticationPrincipal(expression = "id") Long userId) {

        boolean isCompleted = userService.isProfileCompleted(userId);
        return ApiResponse.onSuccess(SuccessStatus._OK, isCompleted);
    }

    // ===== 관심사 관련 API =====
    @Operation(
            summary = "관심사 프로필 완성 (2단계)",
            description = "기본 프로필 완성 후 관심사를 선택하여 프로필 설정을 완료합니다."
    )
    @PostMapping("/profile/interests")
    public ResponseEntity<ApiResponse> completeInterestProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody InterestProfileCompleteRequest request) {

        UserInterestResponse response = userService.completeInterestProfile(userId, request);
        return ApiResponse.onSuccess(SuccessStatus._OK ,response);
    }

    @Operation(
            summary = "관심사 수정",
            description = "사용자의 관심사를 수정합니다."
    )
    @PutMapping("/profile/interests")
    public ResponseEntity<ApiResponse> updateUserInterests(
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody InterestProfileCompleteRequest request) {

        UserInterestResponse response = userService.updateUserInterests(userId, request);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Operation(
            summary = "사용자 관심사 조회",
            description = "현재 사용자의 관심사 목록을 조회합니다."
    )
    @GetMapping("/profile/interests")
    public ResponseEntity<ApiResponse> getUserInterests(
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "id") Long userId) {

        UserInterestResponse response = userService.getUserInterests(userId);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Operation(
            summary = "카테고리별 관심사 전체 조회",
            description = "모든 관심사를 카테고리별로 그룹화하여 조회합니다."
    )
    @GetMapping("/interests")
    public ResponseEntity<ApiResponse> getInterestsGroupedByCategory() {
        Map<String, List<InterestResponse>> interests = interestService.getInterestsGroupedByCategory();
        return ApiResponse.onSuccess(SuccessStatus._OK, interests);
    }


    // 프로필 이미지 presigned url 발급 api
    @Operation(
            summary = "프로필 이미지 Presigned Upload URL 발급",
            description = "사용자의 프로필 이미지를 저장할 presigned upload url을 발급합니다. 발급받은 url으로 put요청하여 s3에 저장합니다.")
    @GetMapping("/profile-image")
    public ResponseEntity<ApiResponse> generatePresignedProfileImageUrl(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestParam String contentType) {
        ProfileResponse.ProfilePresignedUrlResponse dto  = userService.uploadProfileImage(userId, contentType);
        return ApiResponse.onSuccess(SuccessStatus._OK, dto);
    }

    // 프로필 이미지 presigned url 조회 api
    @Operation(
            summary = "프로필 이미지 Presigned Download URL 발급",
            description = "사용자의 프로필 이미지를 바로 볼 수 있는 presigned download url을 발급합니다.")
    @GetMapping("/profile-image-url")
    public ResponseEntity<ApiResponse> getPresignedProfileImageUrl(@AuthenticationPrincipal (expression = "id") Long userId) {
        String url = userService.getProfileImageUrl(userId);
        return ApiResponse.onSuccess(SuccessStatus._OK, url);
    }
}
