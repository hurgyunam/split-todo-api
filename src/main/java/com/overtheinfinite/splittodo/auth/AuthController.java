package com.overtheinfinite.splittodo.auth;
// AuthController.java

import com.overtheinfinite.splittodo.auth.dto.LoginRequest;
import com.overtheinfinite.splittodo.auth.dto.SignupRequest;
import com.overtheinfinite.splittodo.todo.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService; // 가정한 서비스 이름
    private final AuthenticationManager authenticationManager;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

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

        // 1. 인증 토큰 생성
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                );

        try {
            // 2. AuthenticationManager를 통해 인증 시도 (UserDetailsService를 호출)
            Authentication authentication = authenticationManager.authenticate(authToken);

            // 3. 인증 성공 시 SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 4. 세션이 자동 생성되고, 사용자 정보가 세션에 저장됨 (세션 기반의 핵심)
            return ResponseEntity.ok("Session Login successful");

        } catch (Exception e) {
            // 인증 실패 (BadCredentialsException 등)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
