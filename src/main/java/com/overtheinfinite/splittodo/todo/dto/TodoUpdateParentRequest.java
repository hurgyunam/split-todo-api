package com.overtheinfinite.splittodo.todo.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
public class TodoUpdateParentRequest {
    private final Long parentId;

    @Builder
    public TodoUpdateParentRequest(Long parentId) {
        this.parentId = parentId;
    }
}
