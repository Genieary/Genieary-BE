package com.hongik.genieary.common.swagger;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "성공 응답",
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                        name = "onSuccess",
                        summary = "기본 성공 응답",
                        value = """
                    {
                      "isSuccess": true,
                      "code": "COMMON200",
                      "message": "성공입니다.",
                      "result": {
                                      "diaryId": 10,
                                      "content": "오늘 날씨 쥑인다.",
                                      "createdAt": "2025-06-15",
                                      "isLiked": false,
                                      "diaryDate": "2025-06-20"
                                    }
                    }
                    """
                )
        )
)
public @interface SuccessDiaryResponse {}
