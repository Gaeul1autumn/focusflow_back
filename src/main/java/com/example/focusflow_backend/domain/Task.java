package com.example.focusflow_backend.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "tasks")
public class Task {
    @Id
    private String id;

    private String userId;
    private String title;

    // ✨ 1. 이름 변경 (isCompleted -> completed)
    // React에서 { completed: true }로 보내는 것과 이름을 맞춤
    private boolean completed;

    // ✨ 2. 필드 추가 (현재 진행 중인 세션 횟수 유지용)
    // 새로고침 해도 "🔥 2" 같은 진행 상황이 유지되도록 함
    private int focusSessions = 0;

    private LocalDateTime createdAt = LocalDateTime.now();
}
