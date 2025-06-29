package com.hongik.genieary.common.swagger;

public class SwaggerExamples {
    public static final String DIARY_NOT_FOUND_ERROR = """
        {
          "isSuccess": false,
          "code": "DIARY4002",
          "message": "존재하지 않는 일기입니다.",
          "pageInfo": null,
          "result": null
        }
        """;

    public static final String FRIEND_NOT_FOUND_ERROR = """
        {
          "isSuccess": false,
          "code": "FRIEND4002",
          "message": "친구 관계가 존재하지 않습니다.",
          "pageInfo": null,
          "result": null
        }
        """;

    public static final String FRIEND_USER_NOT_FOUND_ERROR = """
        {
          "isSuccess": false,
          "code": "FRIEND4003",
          "message": "상대 유저가 존재하지 않습니다.",
          "pageInfo": null,
          "result": null
        }
        """;

    public static final String FRIEND_REQUEST_ALREADY_EXISTS_ERROR = """
        {
          "isSuccess": false,
          "code": "FRIEND_REQUEST4001",
          "message": "이미 친구 요청을 보냈습니다.",
          "pageInfo": null,
          "result": null
        }
        """;

    public static final String FRIEND_REQUEST_NOT_FOUND_ERROR = """
    {
      "isSuccess": false,
      "code": "FRIEND_REQUEST4003",
      "message": "해당 친구 요청을 찾을 수 없습니다.",
      "pageInfo": null,
      "result": null
    }
    """;

    public static final String INVALID_SEARCH_KEYWORD_ERROR = """
    {
        "isSuccess": false,
            "code": "FRIEND4004",
            "message": "닉네임 검색어는 공백일 수 없습니다.",
            "pageInfo": null,
            "result": null
    }
    """;

    public static final String FRIEND_SEARCH_SUCCESS = """
        {
          "isSuccess": true,
          "code": "COMMON200",
          "message": "성공입니다.",
          "pageInfo": {
            "page": 0,
            "size": 10,
            "hasNext": true,
            "totalElements": 19,
            "totalPages": 2
          },
          "result": [
            {
              "friendId": 7,
              "nickname": "채린",
              "profileImage": null,
              "email": "testt@example.com"
            },
            {
              "friendId": 8,
              "nickname": "채린1",
              "profileImage": null,
              "email": "test@example.com"
            }
          ]
        }
        """;
}
