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
import com.hongik.genieary.domain.enums.ImageType;
import com.hongik.genieary.domain.user.entity.User;
import com.hongik.genieary.domain.user.repository.UserRepository;
import com.hongik.genieary.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryServiceImpl implements DiaryService{

    final private UserRepository userRepository;
    final private DiaryRepository diaryRepository;
    final private CalendarRepository calendarRepository;
    private final S3Service s3Service;

    @Override
    @Transactional
    public DiaryResponseDto.DiaryResultDto createDiary(CustomUserDetails userDetails, DiaryRequestDto.DiaryCreateDto requestDto) {

        User user = userDetails.getUser();
        LocalDate diaryDate = requestDto.getDiaryDate();
        int year = diaryDate.getYear();
        int month = diaryDate.getMonthValue();

        Calendar calendar = calendarRepository.findByUserAndYearAndMonth(user, year, month)
                .orElseGet(() -> calendarRepository.save(Calendar.of(user, year, month)));

        if (diaryRepository.existsByUserIdAndDiaryDate(user.getId(), diaryDate)) {
            throw new GeneralException(ErrorStatus.DIARY_DAY_ALREADY_EXISTS);
        }

        Diary diary = DiaryConverter.toEntity(user, calendar, requestDto);
        diaryRepository.save(diary);
        return DiaryConverter.toResponseDto(diary);
    }


    @Override
    @Transactional
    public DiaryResponseDto.DiaryResultDto updateDiary(Long diaryId, Long userId, DiaryRequestDto.DiaryUpdateDto dto) {
        Diary diary = diaryRepository.findByDiaryIdAndUserId(diaryId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));

        diary.update(dto.getContent(), dto.getIsLiked());

        return DiaryConverter.toResponseDto(diary);
    }

    @Override
    @Transactional
    public void deleteDiary(Long userId, Long diaryId) {
        Diary diary = diaryRepository.findByDiaryIdAndUserId(diaryId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));

        diaryRepository.delete(diary);
    }

    @Override
    public DiaryResponseDto.DiaryResultDto getDiary(Long diaryId, Long userId) {
        Diary diary = diaryRepository.findByDiaryIdAndUserId(diaryId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));
        return DiaryConverter.toResponseDto(diary);
    }

    @Override
    @Transactional
    public DiaryResponseDto.DiaryFaceImageResultDto uploadDiaryFaceImage(Long userId, LocalDate date) {

        int year = date.getYear();
        int month = date.getMonthValue();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Calendar calendar = calendarRepository.findByUserAndYearAndMonth(user, year, month)
                .orElseGet(() -> calendarRepository.save(Calendar.of(user, year, month)));

        Optional<Diary> optionalDiary = diaryRepository.findByUserIdAndDiaryDate(userId, date);
        Diary diary = optionalDiary.orElseGet(() -> Diary.builder()
                .user(user)
                .diaryDate(date)
                .content("")
                .calendar(calendar)
                .isLiked(false)
                .build());

        String fileName = "diary_" + userId + "_" + date + "_" + UUID.randomUUID() + ".jpg";
        diary.uploadImageFileName(fileName);

        diaryRepository.save(diary);

        String url = s3Service.generatePresignedUploadUrl(fileName, ImageType.DIARY);

        return DiaryConverter.toFaceImageResponseDto(diary.getDiaryId(), url);
    }

    @Override
    public String getDiaryFaceImageUrl(Long userId, Long diaryId) {
        Diary diary = diaryRepository.findByDiaryIdAndUserId(diaryId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DIARY_NOT_FOUND));

        String fileName = diary.getImageFileName();
        if (fileName == null || fileName.isBlank()) {
            throw new GeneralException(ErrorStatus.IMAGE_NOT_FOUND);
        }

        return s3Service.generatePresignedDownloadUrl(fileName, ImageType.DIARY);
    }
}
