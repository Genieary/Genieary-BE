package com.hongik.genieary.domain.diary.controller;

import com.hongik.genieary.auth.dto.request.SignupRequest;
import com.hongik.genieary.auth.service.CustomUserDetails;
import com.hongik.genieary.common.status.SuccessStatus;
import com.hongik.genieary.domain.diary.dto.DiaryRequestDto;
import com.hongik.genieary.domain.diary.dto.DiaryResponseDto;
import com.hongik.genieary.domain.diary.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import com.hongik.genieary.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                            schema = @Schema(implementation = SignupRequest.class),
                            examples = @ExampleObject(
                                    name = "일기 예시",
                                    value = "{\"content\": \"오늘 날씨 쥑인다.\", \"isLiked\": \"false\" }"
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "COMMON200",
                            description = "일기 생성 성공했을 경우 나오는 응답입니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DiaryResponseDto.DiaryResultDto.class)
                            )
                    ),

                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "DIARY4001",
                            description = "해당하는 날짜에 일기가 이미 존재하는 경우 나오는 응답입니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "DIARY_DAY_ALREADY_EXISTS",
                                            summary = "해당하는 날짜에 일기 이미 존재",
                                            value = """
                                                    {
                                                      "isSuccess": false,
                                                      "code": "DIARY4001",
                                                      "message": "해당 날짜에 일기가 이미 존재합니다. 수정API를 사용해주세요."
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> createDiary(@AuthenticationPrincipal CustomUserDetails user,
                                                   @Valid @RequestBody DiaryRequestDto.CreateDto requestDto) {
        DiaryResponseDto.DiaryResultDto response = diaryService.createDiary(user, requestDto);

        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }
}

