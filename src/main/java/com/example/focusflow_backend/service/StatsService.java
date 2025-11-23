package com.example.focusflow_backend.service;

import com.example.focusflow_backend.domain.DailyStatistic;
import com.example.focusflow_backend.repository.DailyStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final DailyStatisticRepository dailyStatisticRepository;

    public void updateDailyStats(String userId, long addSeconds, boolean isSessionComplete) {
        // 1. 오늘 날짜 구하기
        //LocalDate today = LocalDate.now(); // 서버 시간 기준 (필요하면 클라이언트에서 날짜를 받을 수도 있음)

        String adjustedDate = LocalDateTime.now()
                .minusHours(4) // 4시간 뒤로 감기
                .toLocalDate() // 날짜만 추출
                .toString();   // "2025-11-23"

        // 2. 오늘 기록이 있는지 확인 (없으면 새로 생성 - upsert 개념)
        DailyStatistic dailyStat = dailyStatisticRepository.findByUserIdAndDate(userId, adjustedDate)
                .orElse(new DailyStatistic(userId, adjustedDate));

        // 3. 데이터 업데이트
        dailyStat.setTotalFocusTime(dailyStat.getTotalFocusTime() + addSeconds);

        if (isSessionComplete) {
            dailyStat.setFocusSessions(dailyStat.getFocusSessions() + 1);
        }

        // 4. 저장
        dailyStatisticRepository.save(dailyStat);
    }
}
