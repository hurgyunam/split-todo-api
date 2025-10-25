package com.overtheinfinite.splittodo.auth.service;

import com.overtheinfinite.splittodo.auth.UserRepository;
import com.overtheinfinite.splittodo.auth.domain.User;
import com.overtheinfinite.splittodo.auth.dto.CustomUserDetails;
import com.overtheinfinite.splittodo.auth.dto.KakaoTokenResponse;
import com.overtheinfinite.splittodo.auth.dto.KakaoUserInfoDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Service
public class KakaoLoginService {
    private final WebClient webClient;
    private final UserRepository userRepository;

    // application.yml에서 설정 값 주입
    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-url}")
    private String tokenUrl;

    @Value("${kakao.user-info-url}")
    private String userInfoUrl;

    public KakaoLoginService(WebClient.Builder webClientBuilder, UserRepository userRepository) {
        // WebClient 인스턴스 생성
        this.webClient = webClientBuilder.baseUrl(tokenUrl).build();
        this.userRepository = userRepository;
    }
    public Authentication loginKakao(String code) {
        String kakaoAccessToken = getKakaoAccessToken(code);
        Long kakaoUniqueId = getKakaoUserId(kakaoAccessToken);

        User user = userRepository.findBySocialId(kakaoUniqueId.toString())
                .orElse(null); // 사용자가 없으면 null 반환;

        if(user != null) {
            UserDetails userDetails = CustomUserDetails.builder()
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .socialId(kakaoUniqueId.toString())
                    .authProvider(User.AuthProvider.KAKAO)
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

    // --- 1. Access Token 획득 ---
    private String getKakaoAccessToken(String code) {

        // POST 요청 본문에 들어갈 파라미터를 설정합니다.
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code); // Next.js에서 받은 인가 코드

        KakaoTokenResponse tokenResponse = webClient
                .post()
                .uri(tokenUrl)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block(); // 블록킹 방식으로 동기 처리 (비동기 처리도 가능)

        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            throw new RuntimeException("카카오 Access Token 발급 실패");
        }

        return tokenResponse.getAccessToken();
    }

    // --- 2. 사용자 ID 획득 ---
    private Long getKakaoUserId(String accessToken) {

        // 사용자 정보 API는 GET 요청이며, Authorization 헤더에 Access Token이 필요합니다.
        KakaoUserInfoDto userInfo = WebClient.builder() // userInfoUrl은 다른 baseUrl이므로 재설정
                .baseUrl(userInfoUrl)
                .build()
                .get()
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .bodyToMono(KakaoUserInfoDto.class)
                .block();

        if (userInfo == null || userInfo.getId() == null) {
            throw new RuntimeException("카카오 사용자 ID 획득 실패");
        }

        return userInfo.getId();
    }
}
