package com.hongik.genieary.domain.schedule.controller;

import com.hongik.genieary.auth.service.CustomUserDetails;
import com.hongik.genieary.common.response.ApiResponse;
import com.hongik.genieary.common.status.SuccessStatus;
import com.hongik.genieary.domain.schedule.dto.ScheduleRequestDto;
import com.hongik.genieary.domain.schedule.dto.ScheduleResponseDto;
import com.hongik.genieary.domain.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "일정 조회", description = "특정 날짜의 일정을 조회합니다.")
    public ResponseEntity<ApiResponse> getSchedules(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<ScheduleResponseDto> schedules = scheduleService.getSchedules(userDetails.getUser(), date);
        return ApiResponse.onSuccess(SuccessStatus._OK, schedules);
    }

    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "일정 삭제", description = "스케줄 ID로 일정을 삭제합니다.")
    public ResponseEntity<ApiResponse> deleteSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long scheduleId) {

        scheduleService.deleteSchedule(userDetails.getUser(), scheduleId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }
}