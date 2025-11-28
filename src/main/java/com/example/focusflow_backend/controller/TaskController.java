package com.example.focusflow_backend.controller;

import com.example.focusflow_backend.domain.Task;
import com.example.focusflow_backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskRepository taskRepository;

    // 1. 할 일 추가 (저장)
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Map<String, String> payload) {
        Task task = new Task();
        task.setUserId(payload.get("userId"));
        task.setTitle(payload.get("title"));
        task.setCompleted(false); // 기본값

        // DB 저장 후 생성된 객체(ID 포함) 반환
        Task savedTask = taskRepository.save(task);
        return ResponseEntity.ok(savedTask);
    }

    // 2. 할 일 삭제 (완료 시 호출)
    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable String taskId) {
        taskRepository.deleteById(taskId);
        return ResponseEntity.ok("할 일 삭제 완료");
    }

    // 3. 목록 조회 (새로고침 시 필요)
    @GetMapping("/{userId}")
    public ResponseEntity<List<Task>> getTasks(@PathVariable String userId) {

        // 1. 기준 시간 계산 (가장 최근의 새벽 4시)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today4AM = now.toLocalDate().atTime(4, 0); // 오늘 새벽 4시

        LocalDateTime cutoffTime;

        if (now.isAfter(today4AM)) {
            // 지금이 새벽 4시 넘었음 -> 오늘 새벽 4시 이전에 만든 건 (어제 만든 거니까) 삭제
            cutoffTime = today4AM;
        } else {
            // 지금이 새벽 4시 전임 (예: 새벽 2시) -> 어제 새벽 4시 이전에 만든 거 삭제
            cutoffTime = today4AM.minusDays(1);
        }

        // 2. 삭제 수행 (청소)
        // "userId"의 데이터 중 "cutoffTime"보다 옛날에 만들어진 건 다 지워라!
        taskRepository.deleteByUserIdAndCreatedAtBefore(userId, cutoffTime);

        //System.out.println("🧹 [자동 청소] 기준 시간: " + cutoffTime + " 이전의 태스크 삭제 완료");


        // 3. 청소된 깨끗한 목록 반환
        return ResponseEntity.ok(taskRepository.findByUserId(userId));
    }

    // ✨ 4. [추가됨] 세션 횟수 증가 (PATCH)
    // 세션 1회가 끝날 때마다 호출해서 DB에 저장 (새로고침 대비)
    @PatchMapping("/{taskId}/session")
    public ResponseEntity<?> updateSessionCount(@PathVariable String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // 기존 횟수 + 1
        task.setFocusSessions(task.getFocusSessions() + 1);

        taskRepository.save(task);
        return ResponseEntity.ok(task); // 업데이트된 태스크 반환
    }

    // 할 일 목록 수동 초기화
    // DELETE /api/tasks/user/{userId}
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteAllTasksByUser(@PathVariable String userId) {
        taskRepository.deleteByUserId(userId);
        return ResponseEntity.ok("사용자의 모든 할 일이 초기화되었습니다.");
    }

}
