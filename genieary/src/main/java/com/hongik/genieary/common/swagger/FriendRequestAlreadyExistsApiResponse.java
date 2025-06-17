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
                responseCode = "4001",
                description = "이미 친구 요청을 보낸 경우 발생하는 에러입니다.",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = com.hongik.genieary.common.response.ApiResponse.class),
                        examples = @ExampleObject(
                                name = "FriendRequestAlreadyExists",
                                summary = "이미 친구 요청 보냄",
                                value = SwaggerExamples.FRIEND_REQUEST_ALREADY_EXISTS_ERROR
                        )
                )
        )
})
public @interface FriendRequestAlreadyExistsApiResponse {}