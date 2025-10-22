package com.overtheinfinite.splittodo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TodoUpdateDetailsRequest {
    private final String title;
    private final Long parentId;

    @Builder
    public TodoUpdateDetailsRequest(String title, Long parentId) {
        this.title = title;
        this.parentId = parentId;
    }
}