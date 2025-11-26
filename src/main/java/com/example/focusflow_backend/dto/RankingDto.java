package com.example.focusflow_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankingDto {
    private int rank;        // 등수 (1, 2, 3...)
    private String nickname; // 유저 닉네임
    private long totalTime;  // 총 집중 시간 (초)
}
