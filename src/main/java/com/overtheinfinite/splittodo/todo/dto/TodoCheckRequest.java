package com.overtheinfinite.splittodo.todo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class TodoCheckRequest {
    private final Long todoId;

    private final boolean isCompleted;

    // 백엔드에서 넘어옴
    @Setter
    private Long userId;

    @Builder
    public TodoCheckRequest(Long todoId, boolean isCompleted) {
        this.todoId = todoId;
        this.isCompleted = isCompleted;
    }
}
