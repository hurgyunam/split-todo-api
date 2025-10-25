package com.overtheinfinite.splittodo.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    // 필요에 따라 refresh_token 등 다른 필드를 추가할 수 있습니다.
}