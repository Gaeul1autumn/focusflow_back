package com.example.focusflow_backend.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Document(collection = "daily_stats")
public class DailyStatistic {
    @Id
    private String id;

    private String userId;      // 누구의 기록인가
    private String date;     // 날짜 (2025-11-23)

    private long totalFocusTime = 0; // 그날의 총 집중 시간
    private int focusSessions = 0;   // 그날의 총 세션 수

    // 생성자 편의 메서드
    public DailyStatistic(String userId, String date) {
        this.userId = userId;
        this.date = date;
    }
}
