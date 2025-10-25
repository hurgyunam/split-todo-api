package com.overtheinfinite.splittodo.todo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TodoUpdateCompletionRequest {
    private final boolean isCompleted;

    @Builder
    public TodoUpdateCompletionRequest(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}