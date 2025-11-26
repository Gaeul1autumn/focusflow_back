package com.example.focusflow_backend.service;

import com.example.focusflow_backend.domain.DailyStatistic;
import com.example.focusflow_backend.domain.User;
import com.example.focusflow_backend.dto.RankingDto;
import com.example.focusflow_backend.repository.DailyStatisticRepository;
import com.example.focusflow_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final DailyStatisticRepository dailyStatisticRepository;
    private final UserRepository userRepository;

    // 1. 일간 랭킹
    public List<RankingDto> getDailyRankings() {
        // 새벽 4시 기준 날짜 계산
        String today = LocalDateTime.now().minusHours(4).toLocalDate().toString();

        // DB에서 오늘 기록 모두 가져옴
        // (Repository에 findAllByDateOrderByTotalFocusTimeDesc 메서드가 필요하지만,
        // 여기서는 기존 findByUserIdAndDate 같은 걸 못 쓰니 로직으로 처리하거나 JPQL/QueryDsl 써야 함.
        // 간단하게 전체 날짜 검색 메서드를 Repository에 추가하겠습니다.)

        List<DailyStatistic> stats = dailyStatisticRepository.findAllByDate(today);

        // 시간순 내림차순 정렬
        stats.sort((a, b) -> Long.compare(b.getTotalFocusTime(), a.getTotalFocusTime()));

        return mapToRankingDto(stats);
    }

    // 2. 주간 랭킹
    public List<RankingDto> getWeeklyRankings() {
        LocalDateTime nowAdjusted = LocalDateTime.now().minusHours(4);

        String tomorrow = nowAdjusted.plusDays(1).toLocalDate().toString();
        String oneWeekAgo =nowAdjusted.minusDays(6).toLocalDate().toString();

        // 최근 7일 데이터 모두 가져오기
        List<DailyStatistic> weeklyData = dailyStatisticRepository.findByDateBetween(oneWeekAgo, tomorrow);

        // 유저별로 시간 합산하기 (Java Stream Grouping)
        Map<String, Long> userTotalTimeMap = weeklyData.stream()
                .collect(Collectors.groupingBy(
                        DailyStatistic::getUserId,
                        Collectors.summingLong(DailyStatistic::getTotalFocusTime)
                ));

        // 합산된 데이터를 리스트로 변환 후 정렬
        List<DailyStatistic> aggregatedStats = new ArrayList<>();
        userTotalTimeMap.forEach((userId, totalTime) -> {
            DailyStatistic stat = new DailyStatistic();
            stat.setUserId(userId);
            stat.setTotalFocusTime(totalTime);
            aggregatedStats.add(stat);
        });

        aggregatedStats.sort((a, b) -> Long.compare(b.getTotalFocusTime(), a.getTotalFocusTime()));

        return mapToRankingDto(aggregatedStats);
    }

    // [헬퍼] 통계 데이터를 랭킹 DTO로 변환 (닉네임 찾기 포함)
    private List<RankingDto> mapToRankingDto(List<DailyStatistic> stats) {
        List<RankingDto> result = new ArrayList<>();
        int rank = 1;

        for (DailyStatistic stat : stats) {
            // 유저 정보 조회 (닉네임 얻기 위해)
            // (실무에서는 캐싱이나 Batch 조회로 최적화하지만, 지금은 loop 조회로 충분)
            // ✅ 수정 후: findByUsername (아이디 문자열로 찾기)
            String nickname = userRepository.findByUsername(stat.getUserId())
                    .map(User::getNickname)
                    .orElse("알 수 없음");

            result.add(new RankingDto(rank++, nickname, stat.getTotalFocusTime()));

            if (rank > 20) break; // 상위 20명만 표시
        }
        return result;
    }
}