package com.overtheinfinite.splittodo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
public class TodoCreateRequest {

    //notnull
    private final String title;

    //nullable
    private final Long parentId;

    // 백엔드에서 넘어옴
    @Setter
    private Long userId;


    // 프론트에서 넘어오는 데이터
    @Builder
    public TodoCreateRequest(String title, Long parentId) {
        this.title = title;
        this.parentId = parentId;
    }
}
