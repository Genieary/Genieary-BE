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
    public DiaryResponseDto.DiaryResultDto createDiary(CustomUserDetails userDetails, DiaryRequestDto.DiaryCreateDto requestDto) {

        User user = userDetails.getUser();
        LocalDate diaryDate = requestDto.getDiaryDate();
        int year = diaryDate.getYear();
        int month = diaryDate.getMonthValue();

        Calendar calendar = calendarRepository.findByUserAndCreatedAtYearAndMonth(user, year, month)
                .orElseGet(() -> calendarRepository.save(
                        Calendar.builder()
                                .user(user)
                                .summary("")
                                .build()
                ));

        if (diaryRepository.existsByUserAndDiaryDate(user, diaryDate)) {
            throw new GeneralException(ErrorStatus.DIARY_DAY_ALREADY_EXISTS);
        }

        Diary diary = DiaryConverter.toEntity(user, calendar, requestDto);
        diaryRepository.save(diary);
        return DiaryConverter.toResponseDto(diary);
    }


    @Override
    @Transactional
    public DiaryResponseDto.DiaryResultDto updateDiary(Long diaryId, User user, DiaryRequestDto.DiaryUpdateDto dto) {
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

    @Override
    public DiaryResponseDto.DiaryResultDto getDiary(Long diaryId, User user) {
        Diary diary = diaryRepository.findByDiaryIdAndUser(diaryId, user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));
        return DiaryConverter.toResponseDto(diary);
    }


}
