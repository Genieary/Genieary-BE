package com.hongik.genieary.domain.schedule.controller;

import com.hongik.genieary.auth.service.CustomUserDetails;
import com.hongik.genieary.common.response.ApiResponse;
import com.hongik.genieary.common.status.SuccessStatus;
import com.hongik.genieary.domain.schedule.dto.ScheduleRequestDto;
import com.hongik.genieary.domain.schedule.dto.ScheduleResponseDto;
import com.hongik.genieary.domain.schedule.dto.ScheduleUpdateDto;
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
    @Operation(summary = "일정 추가", description = "일정을 생성합니다.")
    public ResponseEntity<ApiResponse> addSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ScheduleRequestDto dto) {

        ScheduleResponseDto responseDto = scheduleService.addSchedule(userDetails.getUser(), dto);
        return ApiResponse.onSuccess(SuccessStatus._CREATED, responseDto);
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

    @PatchMapping("/{scheduleId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "일정 수정", description = "일정의 이름, 날짜, 이벤트 여부 중 원하는 항목만 수정합니다.")
    public ResponseEntity<ApiResponse> updateSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long scheduleId,
            @RequestBody ScheduleUpdateDto dto) {

        scheduleService.updateSchedule(userDetails.getUser(), scheduleId, dto);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @GetMapping("/events")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "월별 이벤트 조회", description = "특정 월에 등록된 이벤트를 조회합니다.")
    public ResponseEntity<ApiResponse> getMonthlyEvents(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month) {

        List<ScheduleResponseDto> events = scheduleService.getMonthlyEvents(userDetails.getUser(), year, month);
        return ApiResponse.onSuccess(SuccessStatus._OK, events);
    }
}