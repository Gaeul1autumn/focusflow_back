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

    //특정 날짜의 모든 기록 찾기 (일간 랭킹용)
    List<DailyStatistic> findAllByDate(String date);

    // ✨날짜 범위의 모든 기록 찾기 (주간 랭킹용)
    // (findByUserIdAndDateBetween... 에서 UserId 조건을 뺀 것)
    List<DailyStatistic> findByDateBetween(String startDate, String endDate);
}
