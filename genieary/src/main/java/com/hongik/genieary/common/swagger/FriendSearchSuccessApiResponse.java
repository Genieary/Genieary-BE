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
                responseCode = "200",
                description = "닉네임 검색 결과를 성공적으로 반환합니다.",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponse.class),
                        examples = @ExampleObject(
                                name = "FriendSearchSuccess",
                                summary = "친구 검색 성공",
                                value = SwaggerExamples.FRIEND_SEARCH_SUCCESS
                        )
                )
        )
})
public @interface FriendSearchSuccessApiResponse {
}
