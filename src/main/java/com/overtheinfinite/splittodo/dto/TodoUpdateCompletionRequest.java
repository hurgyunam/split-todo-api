package com.overtheinfinite.splittodo.dto;

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