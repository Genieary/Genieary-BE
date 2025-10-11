package com.hongik.genieary.common.swagger;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses({
        @ApiResponse(
                responseCode = "400",
                description = "싫어요 누른 선물에 좋아요 버튼을 누르면 발생하는 에러입니다.",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = com.hongik.genieary.common.response.ApiResponse.class),
                        examples = @ExampleObject(
                                name = "RecommendAlreadyDislike",
                                summary = "이미 싫어하는 선물",
                                value = SwaggerExamples.RECOMMEND_LIKE_ERROR
                        )
                )
        )
})
public @interface RecommendAlreadyDislike {}