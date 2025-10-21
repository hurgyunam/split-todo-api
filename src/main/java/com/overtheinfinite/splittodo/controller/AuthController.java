package com.overtheinfinite.splittodo.controller;
// AuthController.java

import com.overtheinfinite.splittodo.domain.User;
import com.overtheinfinite.splittodo.dto.LoginRequest;
import com.overtheinfinite.splittodo.dto.SignupRequest;
import com.overtheinfinite.splittodo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService; // 가정한 서비스 이름

    /**
     * 📌 POST /api/v1/auth/signup: 로컬 회원가입 엔드포인트
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {

        try {
            User newUser = userService.registerLocalUser(signupRequest);

            // 성공 응답 (예시로 201 Created와 메시지를 반환)
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully. ID: " + newUser.getId());

        } catch (IllegalArgumentException e) {
            // 중복 등의 비즈니스 로직 오류 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // 기타 서버 오류
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during signup.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {

        // Service에 인증 요청
        boolean isAuthenticated = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

        if (isAuthenticated) {
            // 인증 성공 시, 토큰 발급 로직 등을 추가
            return ResponseEntity.ok("Login successful");
        } else {
            // 인증 실패
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
