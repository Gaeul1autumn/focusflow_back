package com.example.focusflow_backend.controller;

import com.example.focusflow_backend.service.StatsService;
import com.example.focusflow_backend.domain.DailyStatistic;
import com.example.focusflow_backend.repository.DailyStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;
    private final DailyStatisticRepository dailyStatisticRepository;

    // POST /api/stats/{userId}/daily
    @PostMapping("/{userId}/daily")
    public ResponseEntity<?> updateDailyStats(
            @PathVariable String userId,
            @RequestBody Map<String, Object> payload) {

        long addSeconds = ((Number) payload.get("addSeconds")).longValue();
        boolean isSessionComplete = (boolean) payload.get("isSessionComplete");

        statsService.updateDailyStats(userId, addSeconds, isSessionComplete);

        return ResponseEntity.ok("일별 통계 업데이트 완료");
    }


    // ✨ [추가] 통계 조회 (오늘 + 최근 7일)
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable String userId) {
        LocalDate today = LocalDate.now();
        LocalDate oneWeekAgo = today.minusDays(6); // 오늘 포함 최근 7일

        // 1. 오늘 기록
        DailyStatistic todayStat = dailyStatisticRepository.findByUserIdAndDate(userId, today)
                .orElse(new DailyStatistic(userId, today)); // 없으면 빈 객체(0초) 반환

        // 2. 최근 7일 기록
        List<DailyStatistic> weeklyStats = dailyStatisticRepository.findByUserIdAndDateBetweenOrderByDateAsc(userId, oneWeekAgo, today);

        Map<String, Object> response = new HashMap<>();
        response.put("today", todayStat);
        response.put("weekly", weeklyStats);

        return ResponseEntity.ok(response);
    }

}
