package com.overtheinfinite.splittodo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    /**
     * BCrypt 알고리즘으로 비밀번호를 단방향 암호화하는 PasswordEncoder를 빈으로 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt는 강력한 해시 함수와 솔트(Salt)를 자동 적용합니다.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                // 4. POST 요청이므로 CSRF 설정도 고려해야 함 (API 서버라면 보통 비활성화)
                .csrf(AbstractHttpConfigurer::disable) // 예시: REST API 서버라면 비활성화

        // 5. 로그인 설정 (커스텀 로그인 사용 시)
        // .formLogin( ... ) // 필요하다면 커스텀 로그인 페이지 및 처리 설정 추가
        ;

        return http.build();
    }
}