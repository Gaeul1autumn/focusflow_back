package com.example.focusflow_backend.controller;

import com.example.focusflow_backend.domain.Task;
import com.example.focusflow_backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
