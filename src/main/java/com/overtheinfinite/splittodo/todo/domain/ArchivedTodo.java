package com.overtheinfinite.splittodo.todo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "archived_todo")
public class ArchivedTodo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 📌 원본 Todo의 ID를 저장
    @Column(nullable = false, updatable = false)
    private Long originalId;

    // 📌 관계 객체 대신 ID만 저장 (User 엔티티와의 관계 끊기)
    @Column(nullable = false, updatable = false)
    private Long userId;

    // 📌 Parent Todo 객체 대신 ID만 저장 (Self-Reference 관계 끊기)
    private Long parentId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false)
    private boolean isCompleted;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    // 📌 아카이브 처리 시간 기록
    @CreationTimestamp
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime archivedAt;

    /**
     * 편의 생성자: Todo 엔티티를 받아 ArchivedTodo를 생성
     */
    public ArchivedTodo(Todo todo) {
        this.originalId = todo.getId();
        // Lazy Loading 방지 및 관계 끊기를 위해 ID만 추출
        this.userId = todo.getUser().getId();
        this.parentId = todo.getParent() != null ? todo.getParent().getId() : null;

        this.title = todo.getTitle();
        this.isCompleted = todo.isCompleted();
        this.createdAt = todo.getCreatedAt();
        // archivedAt은 @CreationTimestamp에 의해 자동으로 채워집니다.
    }
}