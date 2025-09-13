package com.hongik.genieary.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "interests")
public class InterestDataConfig {

    private List<CategoryData> interests = new ArrayList<>();

    @Data
    public static class CategoryData {
        private String category;
        private List<String> items = new ArrayList<>();
    }
}

