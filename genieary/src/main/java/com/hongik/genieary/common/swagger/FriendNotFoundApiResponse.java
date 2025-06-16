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
                responseCode = "4002",
                description = "존재하지 않는 친구 관계를 요청했을 때 발생하는 에러입니다.",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = com.hongik.genieary.common.response.ApiResponse.class),
                        examples = @ExampleObject(
                                name = "FriendNotFound",
                                summary = "존재하지 않는 친구",
                                value = SwaggerExamples.FRIEND_NOT_FOUND_ERROR
                        )
                )
        )
})
public @interface FriendNotFoundApiResponse {}
