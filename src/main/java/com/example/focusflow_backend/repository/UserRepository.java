package com.example.focusflow_backend.repository;

import com.example.focusflow_backend.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username); // 아이디로 찾기
    boolean existsByUsername(String username);      // 중복 검사
}
