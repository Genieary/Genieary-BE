package com.hongik.genieary.domain.calendar.service;

import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.calendar.dto.CalendarResponseDto;
import com.hongik.genieary.domain.calendar.entity.Calendar;
import com.hongik.genieary.domain.calendar.repository.CalendarRepository;
import com.hongik.genieary.domain.diary.entity.Diary;
import com.hongik.genieary.domain.diary.repository.DiaryRepository;
import com.hongik.genieary.domain.ai.service.OpenAiService;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    public String getSummary(Long userId, Long calendarId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));

        List<Diary> diaries = diaryRespository.findAllByUserIdAndCalendar_CalendarId(userId, calendarId);
        
        String key = "summary:" + userId + ":" + calendarId;
        String cached = redisTemplate.opsForValue().get(key);

        // Redis 캐시 확인
        if (cached != null) return cached;

        // DB 확인
        String summary = calendar.getSummary();
        if (summary != null) {
            redisTemplate.opsForValue().set(key, summary);
            return summary;
        }

        // 저장된 게 없으면 한 달 일기 요약 생성
        String name = user.getNickname();
        String year = String.valueOf(calendar.getYear());
        String month = String.valueOf(calendar.getMonth());

        String combinedText = diaries.stream()
                .map(diary -> diary.getDiaryDate() + "\n" + diary.getContent())
                .collect(Collectors.joining("\n\n"));

        String newSummary = openAiService.summarizeMonthlyDiary(name, year, month, combinedText);

        calendar.updateSummary(newSummary);
        calendarRepository.save(calendar);

        // Redis도 업데이트
        redisTemplate.opsForValue().set(key, newSummary);

        return newSummary;
    }

    @Override
    public CalendarResponseDto.CalendarResultDto getCalendar(Long userId, int year, int month) {

        Calendar calendar = calendarRepository.findByUserIdAndYearAndMonth(userId, year, month)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CALENDAR_NOT_FOUND));

        return CalendarResponseDto.CalendarResultDto.builder()
                .calendarId(calendar.getCalendarId())
                .year(calendar.getYear())
                .month(calendar.getMonth())
                .build();
    }
}
