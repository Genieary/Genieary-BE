package com.hongik.genieary.common.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "페이지를 찾을 수 없습니다."),
    // 입력값 검증 관련 에러
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALID401", "입력값이 올바르지 않습니다."),

    // Auth
    AUTH_EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "AUTH4001", "이미 사용중인 이메일입니다."),
    AUTH_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "AUTH4002", "비밀번호가 일치하지 않습니다."),
    AUTH_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH4003", "회원이 존재하지 않습니다."),
    AUTH_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH4004", "비밀번호가 올바르지 않습니다."),
    AUTH_INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4005", "유효하지 않은 리프레시 토큰입니다."),

    //USER
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER001", "사용자를 찾을 수 없습니다"),
    PROFILE_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "USER002", "이미 프로필이 등록된 사용자입니다"),
    BASIC_PROFILE_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "USER003", "기본 프로필을 먼저 완성해주세요"),
    INTEREST_PROFILE_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "USER004", "이미 관심사 프로필이 완성되었습니다"),

    //USER_PERSONALITY
    PERSONALITY_REQUIRED(HttpStatus.BAD_REQUEST, "P001", "성격을 최소 1개는 선택해야 합니다"),
    PERSONALITY_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "P002", "성격은 최대 3개까지만 선택할 수 있습니다"),

    //USER_INTEREST
    INTEREST_NOT_FOUND(HttpStatus.NOT_FOUND, "I001", "관심사를 찾을 수 없습니다"),
    INTEREST_REQUIRED(HttpStatus.BAD_REQUEST, "I002", "관심사를 최소 1개는 선택해야 합니다"),
    INTEREST_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "I003", "관심사는 최대 5개까지만 선택할 수 있습니다"),
    DUPLICATE_INTEREST_SELECTED(HttpStatus.BAD_REQUEST, "I004", "중복된 관심사를 선택할 수 없습니다"),

    // Diary
    DIARY_DAY_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "DIARY4001", "해당 날짜에 일기가 이미 존재합니다. 수정API를 사용해주세요."),
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY4002", "일기가 존재하지 않습니다. 먼저 생성해주세요."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY4003", "이미지가 존재하지 않습니다. 먼저 업로드해주세요."),

    //Friend
    FRIEND_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "FRIEND4001", "이미 친구인 사용자입니다."),
    FRIEND_NOT_FOUND(HttpStatus.NOT_FOUND, "FRIEND4002", "친구 관계가 존재하지 않습니다."),
    FRIEND_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "FRIEND4003", "상대 유저가 존재하지 않습니다."),
    INVALID_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, "FRIEND4004", "닉네임 검색어는 공백일 수 없습니다."),

    // FriendRequest
    FRIEND_REQUEST_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "FRIEND_REQUEST4001", "이미 친구 요청을 보냈습니다."),
    FRIEND_REQUEST_SELF(HttpStatus.BAD_REQUEST, "FRIEND_REQUEST4002", "자기 자신에게는 친구 요청을 보낼 수 없습니다."),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "FRIEND_REQUEST4003", "해당 친구 요청을 찾을 수 없습니다."),
    FRIEND_REQUEST_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "FRIEND_REQUEST4004", "해당 친구 요청에 접근할 수 없습니다."),
    FRIEND_REQUEST_ALREADY_HANDLED(HttpStatus.BAD_REQUEST, "FRIEND_REQUEST4005", "이미 처리된 친구 요청입니다."),

    // Schedule
    SCHEDULE_CALENDAR_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHEDULE4001", "해당 캘린더를 찾을 수 없습니다."),
    SCHEDULE_DUPLICATED(HttpStatus.BAD_REQUEST, "SCHEDULE4002", "해당 날짜에 동일한 일정이 이미 존재합니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHEDULE4003", "일정이 존재하지 않습니다."),
    SCHEDULE_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "SCHEDULE4004", "해당 일정을 삭제할 권한이 없습니다."),
    SCHEDULE_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "SCHEDULE4005", "요청 값이 유효하지 않습니다."),
    SCHEDULE_INVALID_CALENDAR_DATE_MISMATCH(HttpStatus.BAD_REQUEST, "SCHEDULE4006", "일정의 날짜와 캘린더의 연/월이 일치하지 않습니다."),

    //S3
    S3_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "S34001", "요청한 S3 파일이 존재하지 않습니다."),
    INVALID_IMAGE_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "S34002", "image/jpeg, image/png 확장만 지원됩니다."),

    //Chat
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT4000", "채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CHAT4001", "채팅방에 접근할 권한이 없습니다."),
    SELF_CHAT_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "CHAT4002", "자기 자신과는 채팅방을 생성할 수 없습니다."),
    NOT_FRIEND_RELATIONSHIP(HttpStatus.BAD_REQUEST, "CHAT4003", "두 유저는 친구관계가 아닙니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public String getMessage(String message) {
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(this.getMessage());
    }
}