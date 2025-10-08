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
                description = "검색어가 공백이거나 유효하지 않은 경우 발생하는 에러입니다.",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponse.class),
                        examples = @ExampleObject(
                                name = "InvalidSearchKeyword",
                                summary = "공백 검색어",
                                value = SwaggerExamples.INVALID_SEARCH_KEYWORD_ERROR
                        )
                )
        )
})
public @interface InvalidSearchKeywordApiResponse {
}
