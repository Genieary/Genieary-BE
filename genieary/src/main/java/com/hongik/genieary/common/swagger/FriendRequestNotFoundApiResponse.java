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
                responseCode = "4003",
                description = "해당 친구 요청을 찾을 수 없을 때 발생하는 에러입니다.",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponse.class),
                        examples = @ExampleObject(
                                name = "FriendRequestNotFound",
                                summary = "존재하지 않는 친구 요청",
                                value = SwaggerExamples.FRIEND_REQUEST_NOT_FOUND_ERROR
                        )
                )
        )
})
public @interface FriendRequestNotFoundApiResponse {}