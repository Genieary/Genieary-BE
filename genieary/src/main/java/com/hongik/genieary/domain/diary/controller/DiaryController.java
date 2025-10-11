package com.hongik.genieary.domain.diary.controller;

import com.hongik.genieary.auth.service.CustomUserDetails;
import com.hongik.genieary.common.status.SuccessStatus;
import com.hongik.genieary.common.swagger.DiaryAlreadyExists;
import com.hongik.genieary.common.swagger.DiaryNotFoundApiResponse;
import com.hongik.genieary.common.swagger.SuccessApiResponse;
import com.hongik.genieary.domain.diary.dto.DiaryRequestDto;
import com.hongik.genieary.domain.diary.dto.DiaryResponseDto;
import com.hongik.genieary.domain.diary.service.DiaryService;
import com.hongik.genieary.domain.recommend.dto.RecommendResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import com.hongik.genieary.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Diary API", description = "일기 CRUD API")
@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
public class DiaryController{

    final private DiaryService diaryService;

    @Operation(
            summary = "일기 작성",
            description = "최대 1500자의 일기의 내용을 작성합니다. isLiked는 일기의 저장여부를 나타냅니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = DiaryRequestDto.DiaryCreateDto.class),
                            examples = @ExampleObject(
                                    name = "일기 예시",
                                    value = "{\"content\": \"오늘 날씨 쥑인다.\", \"isLiked\": false, \"diaryDate\": \"2025-06-13\"  }"
                            )
                    )
            )
    )
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @DiaryAlreadyExists
    public ResponseEntity<ApiResponse> createDiary(@AuthenticationPrincipal CustomUserDetails user,
                                                   @Valid @RequestBody DiaryRequestDto.DiaryCreateDto requestDto) {
        DiaryResponseDto.DiaryResultDto response = diaryService.createDiary(user, requestDto);

        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Operation(
            summary = "일기 수정",
            description = "기존 일기의 내용을 수정합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = DiaryRequestDto.DiaryUpdateDto.class),
                            examples = @ExampleObject(
                                    name = "일기 수정 예시",
                                    value = "{\"content\": \"수정된 내용입니다.\", \"isLiked\": true }"
                            )
                    )
            )
    )
    @PatchMapping("/{diaryId}")
    @PreAuthorize("isAuthenticated()")
    @DiaryNotFoundApiResponse
    public ResponseEntity<ApiResponse> updateDiary(@PathVariable Long diaryId,
                                                   @AuthenticationPrincipal CustomUserDetails user,
                                                   @Valid @RequestBody DiaryRequestDto.DiaryUpdateDto requestDto) {

        DiaryResponseDto.DiaryResultDto response = diaryService.updateDiary(diaryId, user.getUser().getId(), requestDto);

        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{diaryId}")
    @Operation(summary = "일기 삭제", description = "일기 ID에 해당하는 일기를 삭제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)
            )
    )
    @SuccessApiResponse
    @DiaryNotFoundApiResponse
    public ResponseEntity<ApiResponse> deleteDiary(@AuthenticationPrincipal CustomUserDetails user,
                                                   @PathVariable Long diaryId) {
        diaryService.deleteDiary(user.getUser().getId(), diaryId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @GetMapping("/{diaryId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "일기 조회", description = "일기 ID에 해당하는 일기를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DiaryResponseDto.DiaryResultDto.class)
            )
    )
    @DiaryNotFoundApiResponse
    public ResponseEntity<ApiResponse> getDiary(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long diaryId) {
        DiaryResponseDto.DiaryResultDto response = diaryService.getDiary(diaryId, userDetails.getUser().getId());
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    // 일기 얼굴 presigned url 발급 api
    @Operation(
            summary = "일기 얼굴 사진 Presigned Upload URL 발급",
            description = "사용자의 일기 얼굴 사진을 저장할 presigned upload url을 발급합니다. 발급받은 url으로 put요청하여 s3에 저장합니다.")
    @PostMapping("/{date}/diary-face")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DiaryResponseDto.DiaryFaceImageResultDto.class)
            )
    )
    public ResponseEntity<ApiResponse> uploadDiaryFaceImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String contentType) {
        DiaryResponseDto.DiaryFaceImageResultDto dto  = diaryService.uploadDiaryFaceImage(userDetails.getUser().getId(), date, contentType);
        return ApiResponse.onSuccess(SuccessStatus._OK, dto);
    }

    // 일기 얼굴 presigned url 발급 api
    @Operation(
            summary = "일기 얼굴 사진 Presigned Download URL 발급",
            description = "사용자의 일기 얼굴 사진을 바로 볼 수 있는 presigned download url을 발급합니다.")
    @GetMapping("/{diaryId}/diary-face-url")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DiaryResponseDto.DiaryFaceImageResultDto.class)
            )
    )
    public ResponseEntity<ApiResponse> getDiaryFaceImageUrl(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long diaryId) {
        DiaryResponseDto.DiaryFaceImageResultDto dto = diaryService.getDiaryFaceImageUrl(userDetails.getUser().getId(), diaryId);
        return ApiResponse.onSuccess(SuccessStatus._OK, dto);
    }

}

