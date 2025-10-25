package com.overtheinfinite.splittodo.todo.dto;

import com.overtheinfinite.splittodo.todo.domain.Todo;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TodoResponse {
    private final Long id;
    private final String title;
    private final boolean isCompleted;
    private final Long userId; // User ID만 노출
    private final Long parentId; // Parent Todo ID만 노출
    private final LocalDateTime createdAt;

    // 생성자나 빌더 메서드 (Todo 엔티티를 받아 DTO로 변환)
    public TodoResponse(Todo todo) {
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.isCompleted = todo.isCompleted();
        // 핵심: 엔티티 객체 대신 ID만 꺼내서 DTO에 담습니다.
        this.userId = todo.getUser().getId();
        this.parentId = (todo.getParent() != null) ? todo.getParent().getId() : null;
        this.createdAt = todo.getCreatedAt();
    }
}
