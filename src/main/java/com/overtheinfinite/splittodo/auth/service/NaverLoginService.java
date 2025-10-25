package com.overtheinfinite.splittodo.auth.service;

import com.overtheinfinite.splittodo.auth.UserRepository;
import com.overtheinfinite.splittodo.auth.domain.User;
import com.overtheinfinite.splittodo.auth.dto.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.http.HttpStatusCode;
import java.util.Map;
import java.util.UUID;

@Service
public class NaverLoginService {
    private final WebClient webClient;
    private final UserRepository userRepository;
    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${naver.redirect-uri}")
    private String redirectUri;

    @Value("${naver.token-url}")
    private String tokenUrl;

    @Value("${naver.user-info-url}")
    private String userInfoUrl;

    public NaverLoginService(WebClient.Builder webClientBuilder, UserRepository userRepository) {
        // WebClient 인스턴스 생성
        this.webClient = webClientBuilder.baseUrl(tokenUrl).build();
        this.userRepository = userRepository;
    }

    public String createState() {

        return UUID.randomUUID().toString();
    }

    public Authentication loginNaver(String code, String state) {
        String naverAccessToken = getNaverAccessToken(code, state);
        String naverUniqueId = getNaverUserId(naverAccessToken);

        User user = userRepository.findBySocialId(naverUniqueId)
                .orElse(null); // 사용자가 없으면 null 반환;

        if(user != null) {
            UserDetails userDetails = CustomUserDetails.builder()
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .socialId(naverUniqueId)
                    .authProvider(User.AuthProvider.NAVER)
                    .build();

            return new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
        } else { // 회원가입 진행
            return null;
        }
    }

    /**
     * 1. Naver로부터 받은 인증 코드(oauthCode)를 사용하여 Access Token을 요청하고 반환합니다.
     * WebClient를 사용하여 비동기 요청 후, .block()으로 동기 결과를 반환합니다.
     * @param oauthCode Naver로부터 받은 인증 코드 (Authorization Code)
     * @param state CSRF 방지를 위해 요청 시 사용했던 state 값
     * @return 발급된 Naver Access Token
     */
    public String getNaverAccessToken(String oauthCode, String state) {
        // 1. 요청 URI 생성
        String uri = UriComponentsBuilder.fromUriString(tokenUrl)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", oauthCode)
                .queryParam("state", state)
                .toUriString();

        try {
            // 2. WebClient를 사용한 비동기 HTTP 요청
            Map responseMap = webClient.post()
                    .uri(uri)
                    .retrieve()
                    // 응답 상태 코드가 오류(4xx, 5xx)일 경우 예외 처리
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        System.err.println("Naver Access Token 요청 실패. 상태 코드: " + clientResponse.statusCode());
                        // 오류 발생 시 Mono.error를 반환하여 try-catch 블록으로 던집니다.
                        return clientResponse.createException();
                    })
                    .bodyToMono(Map.class) // 응답 본문을 Map 형태로 받음
                    .block(); // 3. 동기적으로 결과를 기다림

            // 4. Access Token 추출
            if (responseMap != null) {
                return (String) responseMap.get("access_token");
            }

        } catch (Exception e) {
            System.err.println("Naver Access Token 처리 중 예외 발생: " + e.getMessage());
        }
        return null;
    }
    public String getNaverUserId(String accessToken) {
        return null;
    }
}
