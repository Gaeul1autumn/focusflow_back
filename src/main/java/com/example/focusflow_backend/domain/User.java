package com.example.focusflow_backend.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;       // MongoDB ObjectId

    private String username; // 아이디
    private String password; // 암호화된 비밀번호
    private String nickname; // 닉네임

    private String role;     // 권한 (ROLE_USER)
}
