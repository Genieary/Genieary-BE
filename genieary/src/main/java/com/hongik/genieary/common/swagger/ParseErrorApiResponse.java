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
                description = "AI응답 파싱 에러입니다.",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = com.hongik.genieary.common.response.ApiResponse.class),
                        examples = @ExampleObject(
                                name = "ParseError",
                                summary = "Json 응답 parse 실패",
                                value = SwaggerExamples.JSON_PARSE_ERROR
                        )
                )
        )
})
public @interface ParseErrorApiResponse {}