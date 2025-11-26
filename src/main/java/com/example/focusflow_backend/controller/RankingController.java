package com.example.focusflow_backend.controller;

import com.example.focusflow_backend.dto.RankingDto;
import com.example.focusflow_backend.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ranks")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/daily")
    public ResponseEntity<List<RankingDto>> getDailyRanks() {
        return ResponseEntity.ok(rankingService.getDailyRankings());
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<RankingDto>> getWeeklyRanks() {
        return ResponseEntity.ok(rankingService.getWeeklyRankings());
    }
}