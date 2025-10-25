package com.overtheinfinite.splittodo.todo.domain;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User {

    // 1. 기본 ID (Primary Key)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // MariaDB AUTO_INCREMENT
    private Long id;

    // 2. 닉네임 (소셜, 일반 공통)
    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    // 3. 일반 로그인 필드 (ID/PW)

    // 일반 로그인을 위한 사용자 ID (이메일 등이 될 수 있음)
    // 소셜 로그인 사용자는 null 허용
    @Column(unique = true, length = 100)
    private String username;

    // 비밀번호 (일반 로그인 사용자만 사용, 소셜 로그인 사용자는 null 허용)
    private String password;

    // 4. 인증 방식 구분 (핵심!)
    @Enumerated(EnumType.STRING) // DB에 문자열로 저장
    @Column(nullable = false, length = 10)
    private AuthProvider authProvider;

    // 5. 소셜 로그인 고유 ID (카카오/네이버 등에서 받은 ID)
    // 카카오/네이버에서 제공하는 사용자의 고유 ID를 저장합니다.
    // authProvider와 socialId를 묶어서 복합 고유 인덱스를 설정하는 것이 좋습니다.
    private String socialId;


    // --- ENUM 정의 ---
    public enum AuthProvider {
        LOCAL,   // 일반 ID/PW 로그인
        KAKAO,   // 카카오 로그인
        NAVER    // 네이버 로그인
    }

    // --- 생성자 ---

    // 📌 일반(LOCAL) 사용자 생성자
    @Builder(builderMethodName = "localBuilder")
    public User(String username, String password, String nickname) {
        this.username = username;
        this.password = password; // 실제로는 암호화된 비밀번호가 저장됩니다.
        this.nickname = nickname;
        this.authProvider = AuthProvider.LOCAL;
    }

    // 📌 소셜(KAKAO/NAVER) 사용자 생성자
    @Builder(builderMethodName = "socialBuilder")
    public User(String socialId, String nickname, AuthProvider authProvider) {
        // 일반 로그인 필드는 null로 남겨둡니다.
        this.socialId = socialId;
        this.nickname = nickname;
        this.authProvider = authProvider;
    }
}