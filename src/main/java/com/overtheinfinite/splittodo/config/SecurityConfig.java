package com.overtheinfinite.splittodo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    /**
     * BCrypt 알고리즘으로 비밀번호를 단방향 암호화하는 PasswordEncoder를 빈으로 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt는 강력한 해시 함수와 솔트(Salt)를 자동 적용합니다.
        return new BCryptPasswordEncoder();
    }
}