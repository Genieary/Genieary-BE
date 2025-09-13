package com.hongik.genieary.common.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus {

    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),
    _CREATED(HttpStatus.CREATED, "COMMON201", "리소스가 성공적으로 생성되었습니다."),

    //프로필 관련
    PROFILE_COMPLETED(HttpStatus.OK, "S101", "프로필이 성공적으로 완성되었습니다"),
    PROFILE_UPDATED(HttpStatus.OK, "S102", "프로필이 성공적으로 업데이트되었습니다"),
    PROFILE_RETRIEVED(HttpStatus.OK, "S103", "프로필 조회 완료"),
    PROFILE_STATUS_RETRIEVED(HttpStatus.OK, "S104", "프로필 상태 조회 완료");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}