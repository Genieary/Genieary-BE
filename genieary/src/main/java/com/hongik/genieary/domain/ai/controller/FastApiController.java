package com.hongik.genieary.domain.ai.controller;

import com.hongik.genieary.common.response.ApiResponse;
import com.hongik.genieary.common.status.SuccessStatus;
import com.hongik.genieary.common.swagger.ParseErrorApiResponse;
import com.hongik.genieary.domain.ai.dto.FastApiResponseDto;
import com.hongik.genieary.domain.ai.service.FaceAnalysisService;
import com.hongik.genieary.domain.ai.service.FastApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
@Tag(name = "FastAPI", description = "fastapi 관련 API")
public class FastApiController {

    private final FastApiService fastApiService;
    private final FaceAnalysisService faceAnalysisService;

    @PostMapping(value = "/face-analysis")
    @Operation(
            summary = "얼굴사진과 다이어리기반 감정분석",
            description = "사용자의 얼굴 사진 링크 기반으로 감정을 분석해줍니다. 사용자가 선택한 날짜에 일기가 있을 경우 일기내용을 고려하여 분석해줍니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FastApiResponseDto.FaceAnalysisResponseDto.class)
            )
    )
    @ParseErrorApiResponse
    public ResponseEntity<ApiResponse> analyzeFace(@AuthenticationPrincipal(expression = "id") Long userId,
                                                   LocalDate diaryDate,
                                                   String faceImg){

        FastApiResponseDto.FaceAnalysisResponseDto dto = fastApiService.analyzeFace(userId, diaryDate, faceImg);

        return ApiResponse.onSuccess(SuccessStatus._OK, dto);
    }

    @GetMapping("/{diaryId}")
    @Operation(
            summary = "감정분석조회",
            description = "다이어리 아이디로 감정분석 결과를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FastApiResponseDto.FaceAnalysisResponseDto.class)
            )
    )
    public ResponseEntity<ApiResponse> getFaceAnalysis(
            @PathVariable Long diaryId) {

        FastApiResponseDto.FaceAnalysisResponseDto dto = faceAnalysisService.getFaceAnalysisByDiaryId(diaryId);

        return ApiResponse.onSuccess(SuccessStatus._OK, dto);
    }
}

