package com.hongik.genieary.domain.schedule.controller;

import com.hongik.genieary.auth.service.CustomUserDetails;
import com.hongik.genieary.common.response.ApiResponse;
import com.hongik.genieary.common.status.SuccessStatus;
import com.hongik.genieary.domain.schedule.dto.ScheduleRequestDto;
import com.hongik.genieary.domain.schedule.service.ScheduleService;
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

@Tag(name = "Schedule API", description = "일정 CRUD API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "일정 추가", description = "캘린더에 일정을 추가합니다.")
    public ResponseEntity<ApiResponse> addSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ScheduleRequestDto dto) {

        scheduleService.addSchedule(dto);
        return ApiResponse.onSuccess(SuccessStatus._CREATED);
    }
}