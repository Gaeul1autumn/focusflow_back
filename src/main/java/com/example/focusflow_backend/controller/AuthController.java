package com.example.focusflow_backend.controller;

import com.example.focusflow_backend.domain.User;
import com.example.focusflow_backend.dto.LoginRequest;
import com.example.focusflow_backend.dto.SignupRequest;
import com.example.focusflow_backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            authService.signup(request);
            return ResponseEntity.ok("회원가입 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            User user = authService.login(request);

            // 세션 생성 (이미 있으면 가져오고 없으면 생성)
            HttpSession session = httpRequest.getSession(true);
            // 세션에 사용자 정보 저장 (보안을 위해 비밀번호는 제외하거나 필요한 정보만 저장)
            session.setAttribute("LOGIN_USER", user);
            session.setMaxInactiveInterval(1800); // 30분 유지

            Map<String, Object> response = new HashMap<>();
            response.put("message", "로그인 성공");
            response.put("nickname", user.getNickname());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 날리기
        }
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 세션 확인 (새로고침 시 로그인 유지용)
    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("LOGIN_USER") == null) {
            return ResponseEntity.status(401).body("로그인되지 않음");
        }
        User user = (User) session.getAttribute("LOGIN_USER");

        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("nickname", user.getNickname());

        return ResponseEntity.ok(response);
    }
}
