package com.overtheinfinite.splittodo.auth.dto;
import lombok.Getter;

@Getter
public class KakaoUserInfoDto {
    private Long id; // 카카오 고유 회원 번호

    // 필요에 따라 kakao_account(이메일, 닉네임) 등 다른 필드를 추가할 수 있습니다.
}