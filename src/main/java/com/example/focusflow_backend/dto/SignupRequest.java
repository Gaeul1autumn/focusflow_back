package com.example.focusflow_backend.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String password;
    private String nickname;
}

