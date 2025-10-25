package com.overtheinfinite.splittodo.auth.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String username; // 사용자가 입력한 ID (일반적으로 이메일)
    private String password; // 평문 비밀번호 (RAW)
    private String nickname; // 사용자가 설정한 닉네임
}