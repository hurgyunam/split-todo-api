package com.overtheinfinite.splittodo.todo.domain;

import com.overtheinfinite.splittodo.auth.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // 모든 필드를 포함하는 생성자 (테스트용)
@Table(name = "todo") // 테이블 이름을 명시적으로 지정
public class Todo {

    // 1. auto increment id (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // MariaDB의 AUTO_INCREMENT에 최적화
    private Long id;

    // 📌 1. User 엔티티 참조 필드 (필수)
    // Many-to-One: 여러 Todo가 한 명의 User에 속합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // 외래 키 컬럼명은 user_id로 설정
    private User user; // Todo의 주인(User)

    // 2. title (할 일 제목)
    @Column(nullable = false, length = 255)
    private String title;

    // 3. isCompleted (완료 여부)
    @Column(nullable = false)
    private boolean isCompleted = false; // 기본값은 false로 설정

    // 📌 추가: 완료 시점 기록 (isCompleted가 true가 된 시점)
    @Column(columnDefinition = "TIMESTAMP") // NULL 허용 (아직 완료되지 않은 경우)
    private LocalDateTime completedAt;

    // 4. parentId (셀프 참조 관계)
    // 자기 자신(Todo)을 참조하는 Many-to-One 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Todo parent;

    // 5. createdAt (생성 시간)
    // 엔티티가 저장될 때 자동으로 현재 시간을 기록
    @CreationTimestamp
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP") // 생성 후 업데이트 불가
    private LocalDateTime createdAt;

    /**
     * 편의 메서드 (Optional): Todo 생성자
     */
    @Builder
    public Todo(String title, Todo parent) {
        this.title = title;
        this.parent = parent;
        this.isCompleted = false;
    }
}