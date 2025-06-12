package com.hongik.genieary.domain.diary.service;

import com.hongik.genieary.auth.service.CustomUserDetails;
import com.hongik.genieary.common.exception.GeneralException;
import com.hongik.genieary.common.status.ErrorStatus;
import com.hongik.genieary.domain.calendar.entity.Calendar;
import com.hongik.genieary.domain.calendar.repository.CalendarRepository;
import com.hongik.genieary.domain.diary.converter.DiaryConverter;
import com.hongik.genieary.domain.diary.dto.DiaryRequestDto;
import com.hongik.genieary.domain.diary.dto.DiaryResponseDto;
import com.hongik.genieary.domain.diary.entity.Diary;
import com.hongik.genieary.domain.diary.repository.DiaryRepository;
import com.hongik.genieary.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryServiceImpl implements DiaryService{

    final private DiaryRepository diaryRepository;
    final private CalendarRepository calendarRepository;

    @Override
    @Transactional
    public DiaryResponseDto.DiaryResultDto createDiary(CustomUserDetails userDetails, DiaryRequestDto.CreateDto requestDto) {

        User user = userDetails.getUser();
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        Calendar calendar = calendarRepository.findByUserAndCreatedAtYearAndMonth(user, year, month)
                .orElseGet(() -> calendarRepository.save(
                        Calendar.builder()
                                .user(user)
                                .summary("")
                                .build()
                ));

        if (diaryRepository.existsByUserAndCreatedAt(user, today)) {
            throw new GeneralException(ErrorStatus.DIARY_DAY_ALREADY_EXISTS);
        }

        Diary diary = DiaryConverter.toEntity(user, calendar, requestDto);
        diaryRepository.save(diary);
        return DiaryConverter.toResponseDto(diary);
    }


    @Override
    @Transactional
    public DiaryResponseDto.DiaryResultDto updateDiary(Long diaryId, User user, DiaryRequestDto.UpdateDto dto) {
        Diary diary = diaryRepository.findByDiaryIdAndUser(diaryId, user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));

        diary.update(dto.getContent(), dto.getIsLiked());

        return DiaryConverter.toResponseDto(diary);
    }

    @Override
    @Transactional
    public void deleteDiary(User user, Long diaryId) {
        Diary diary = diaryRepository.findByDiaryIdAndUser(diaryId, user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));

        diaryRepository.delete(diary);
    }


}
