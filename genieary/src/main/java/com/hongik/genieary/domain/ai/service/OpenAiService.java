package com.hongik.genieary.domain.ai.service;

import com.hongik.genieary.common.util.PromptLoader;
import com.hongik.genieary.infra.openai.OpenAiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final PromptLoader promptLoader;
    private final OpenAiClient openAiClient; // RestTemplate이나 WebClient로 감싼 API 호출기

    public String summarizeMonthlyDiary(String name, String year, String month, String diaryText) {
        String template = promptLoader.loadPrompt("monthly_summary_prompt.txt");

        String prompt = template
                .replace("{name}", name)
                .replace("{year}", year)
                .replace("{month}", month)
                .replace("{diary}", diaryText);

        return openAiClient.requestChatCompletion(prompt);
    }
}
