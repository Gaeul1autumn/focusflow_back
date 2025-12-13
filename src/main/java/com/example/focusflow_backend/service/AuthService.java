package com.example.focusflow_backend.service;

import com.example.focusflow_backend.domain.User;
import com.example.focusflow_backend.dto.LoginRequest;
import com.example.focusflow_backend.dto.SignupRequest;
import com.example.focusflow_backend.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // 추가됨
import org.springframework.security.core.userdetails.UserDetailsService; // 추가됨
import org.springframework.security.core.userdetails.UsernameNotFoundException; // 추가됨
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.Optional;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // DB에서 내 유저 찾기
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 내 유저 정보(User)를 스프링 시큐리티가 이해하는 객체(UserDetails)로 변환해서 리턴
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // DB에 저장된 암호화된 비밀번호
                .authorities(Collections.emptyList()) // 권한 설정 (필요하면 user.getRole() 넣기)
                .build();
    }

    // 회원가입
    public void signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        // 비밀번호 암호화 저장
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setRole("ROLE_USER");

        userRepository.save(user);
    }

    // 로그인 (세션 처리)
    public User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 암호화된 비밀번호 비교
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        UserDetails userDetails = loadUserByUsername(user.getUsername());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // 이 줄이 있어야 다음 페이지로 넘어갈 때 "로그인 된 상태"로 인식됩니다.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return user;
    }
}
