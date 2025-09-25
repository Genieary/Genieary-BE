package com.hongik.genieary.domain.recommend;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {
    GIFT,
    FOOD,
    ACTIVITY,
    FUN;

    @JsonCreator
    public static Category from(String value) {
        return Category.valueOf(value.toUpperCase());
    }
}
