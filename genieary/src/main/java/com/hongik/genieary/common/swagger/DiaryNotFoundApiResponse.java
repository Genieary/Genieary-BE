package com.hongik.genieary.common.swagger;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses({
        @ApiResponse(
                responseCode = "404",
                description = "존재하지 않는 일기를 요청했을 때 발생하는 에러입니다.",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = com.hongik.genieary.common.response.ApiResponse.class),
                        examples = @ExampleObject(
                                name = "DiaryNotFound",
                                summary = "존재하지 않는 일기",
                                value = SwaggerExamples.DIARY_NOT_FOUND_ERROR
                        )
                )
        )
})
public @interface DiaryNotFoundApiResponse {}
