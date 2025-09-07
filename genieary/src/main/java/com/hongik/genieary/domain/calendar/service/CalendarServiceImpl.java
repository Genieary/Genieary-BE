package com.hongik.genieary.domain.calendar.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.calendar.entity.Calendar;
import com.hongik.genieary.domain.calendar.repository.CalendarRepository;
import com.hongik.genieary.domain.diary.entity.Diary;
import com.hongik.genieary.domain.diary.repository.DiaryRepository;
import com.hongik.genieary.domain.ai.service.OpenAiService;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarServiceImpl implements CalendarService{

    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRespository;
    private final OpenAiService openAiService;

    @Override
    public String getSummary(Long userId, Long calendarId){

        List<Diary> diaries = diaryRespository.findAllByUserIdAndCalendar_CalendarId(userId, calendarId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));

        String name = user.getNickname();
        String year = String.valueOf(calendar.getYear());
        String month = String.valueOf(calendar.getMonth());

        String combinedText = diaries.stream()
                .map(diary -> diary.getDiaryDate() + "\n" + diary.getContent())
                .collect(Collectors.joining("\n\n"));

        String summary = openAiService.summarizeMonthlyDiary(name, year, month, combinedText);

        return summary;
    }
}
