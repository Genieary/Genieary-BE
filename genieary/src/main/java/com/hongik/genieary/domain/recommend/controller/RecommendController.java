package com.hongik.genieary.domain.recommend.controller;

import com.hongik.genieary.common.response.ApiResponse;
import com.hongik.genieary.common.status.SuccessStatus;
import com.hongik.genieary.common.swagger.RecommendParseErrorApiResponse;
import com.hongik.genieary.common.swagger.SuccessRecommendResponse;
import com.hongik.genieary.domain.recommend.Category;
import com.hongik.genieary.domain.recommend.dto.RecommendResponseDto;
import com.hongik.genieary.domain.recommend.service.RecommendService;
import com.hongik.genieary.domain.user.dto.request.ProfileCompleteRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "RECOMMEND API", description = "선물추천 관련 API")
@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {
    final private RecommendService recommendService;

    @Operation(
            summary = "사용자 맞춤 선물 추천",
            description = "사용자가 선택한 category에서 선물 3개를 추천해줍니다. 기념일은 선택하면 기념일에 맞는 선물을 추천해줍니다.")
    @PostMapping
    @SuccessRecommendResponse
    @RecommendParseErrorApiResponse
    public ResponseEntity<ApiResponse> recommendGifts(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestParam Category category,
            @RequestParam(required = false) String event){

        List<RecommendResponseDto.GiftResultDto> gifts = recommendService.getRecommendations(userId, category, event);

        return ApiResponse.onSuccess(SuccessStatus._OK, gifts);
    }

    @Operation(
            summary = "추천받은 선물 좋아요",
            description = "추천 받은 선물 중에 원하는 선물에 좋아요를 남깁니다. 좋아요를 누른 선물을 저장된 선물 페이지에서 볼 수 있습니다.")
    @PostMapping("{recommendId}/like")
    public ResponseEntity<ApiResponse> togleLikeGift(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long recommendId){

        RecommendResponseDto.LikeResultDto dto = recommendService.togleLikeGift(userId, recommendId);

        return ApiResponse.onSuccess(SuccessStatus._OK, dto);
    }

    @Operation(
            summary = "추천받은 선물 싫어요",
            description = "추천 받은 선물 중에 싫어하는 선물에 싫어요를 남깁니다. 싫어요를 누르면 당일에는 같은 선물을 다시 추천받지 않습니다.")
    @PostMapping("{recommendId}/dislike")
    public ResponseEntity<ApiResponse> togleHateGift(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long recommendId){

        RecommendResponseDto.HateResultDto dto = recommendService.togleHateGift(userId, recommendId);

        return ApiResponse.onSuccess(SuccessStatus._OK, dto);
    }
}
