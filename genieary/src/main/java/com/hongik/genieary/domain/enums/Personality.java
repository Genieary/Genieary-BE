package com.hongik.genieary.domain.enums;

public enum Personality {
    PLANNED("계획적인"),
    ENERGETIC("활기있는"),
    SOCIABLE("사교적인"),
    CALM("차분한"),
    ANALYTICAL("분석적인"),
    IMPULSIVE("충동적인"),
    SERIOUS("진지한"),
    PASSIONATE("열정적인"),
    PERFECTIONIST("완벽주의자"),
    HONEST("솔직한"),
    RESTRAINED("절제된"),
    AGGRESSIVE("공격적인"),
    NEAT("깔끔한"),
    JEALOUS("질투많은"),
    FRUGAL("검소한"),
    MELANCHOLIC("우울한"),
    CARELESS("덜렁이"),
    GREEDY("욕심쟁이"),
    INTROVERTED("내성적"),
    EXTROVERTED("외향적"),
    SIMPLE("단순적"),
    LONER("외톨이");

    private final String description;

    Personality(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}