package com.hongik.genieary.domain.calendar.controller;

import com.hongik.genieary.auth.service.CustomUserDetails;
import com.hongik.genieary.common.response.ApiResponse;
import com.hongik.genieary.common.status.SuccessStatus;
import com.hongik.genieary.common.swagger.DiaryNotFoundApiResponse;
import com.hongik.genieary.common.swagger.SuccessApiResponse;
import com.hongik.genieary.common.swagger.SuccessSummaryResponse;
import com.hongik.genieary.domain.calendar.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Calendar API", description = "한 달 요약 API")
@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    // 한 달 일기 요약 api
    @Operation(
            summary = "한 달 일기 내용을 요약",
            description = "사용자의 한 달 일기 내용을 요약해주는 api입니다.")
    @GetMapping("/summary/{calendarId}")
    @SuccessSummaryResponse
    @DiaryNotFoundApiResponse
    public ResponseEntity<ApiResponse> getDiarySummary(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long calendarId) {
        String summary = calendarService.getSummary(userDetails.getUser().getId(), calendarId);
        return ApiResponse.onSuccess(SuccessStatus._OK, summary);
    }

}
