package com.example.focusflow_backend.repository;

import com.example.focusflow_backend.domain.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {

    // 1. 기본적인 save(), findById(), delete() 등은 MongoRepository가 자동으로 만들어줍니다.

    // 2. 특정 유저의 할 일 목록만 가져오는 메서드 (나중에 리스트 조회할 때 필요)
    // "SELECT * FROM tasks WHERE userId = ?" 와 같습니다.
    List<Task> findByUserId(String userId);

    // (선택) 완료된 작업만 가져오기
    List<Task> findByUserIdAndCompleted(String userId, boolean completed);

    // 특정 유저의 모든 할 일 삭제
    void deleteByUserId(String userId);
}
