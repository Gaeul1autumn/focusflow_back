package com.example.focusflow_backend.repository;

import com.example.focusflow_backend.domain.DailyStatistic;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.util.List;

public interface DailyStatisticRepository extends MongoRepository<DailyStatistic, String> {

    // "이 유저"의 "특정 날짜" 기록이 있는지 찾기
    Optional<DailyStatistic> findByUserIdAndDate(String userId, String date);

    // (나중을 위해) 특정 유저의 기간별 조회 (예: 최근 7일)
    List<DailyStatistic> findByUserIdAndDateBetweenOrderByDateAsc(String userId, String startDate, String endDate);
}
