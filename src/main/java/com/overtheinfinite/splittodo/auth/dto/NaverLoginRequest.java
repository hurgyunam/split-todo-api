package com.overtheinfinite.splittodo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter // Jackson이 값을 주입할 수 있도록 Setter 추가
@NoArgsConstructor // Jackson이 객체 인스턴스를 만들 수 있도록 기본 생성자 추가
@AllArgsConstructor // 선택적으로 모든 인자를 받는 생성자 추가
public class NaverLoginRequest {
    private String code;
}
