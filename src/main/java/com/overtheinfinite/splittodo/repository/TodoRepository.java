package com.overtheinfinite.splittodo.repository;

import com.overtheinfinite.splittodo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    // 추가적인 사용자 정의 쿼리가 필요하면 여기에 선언합니다.
    List<Todo> findByUser_Id(Long userId);

    // isCompleted가 true이고, completedAt이 특정 시간 이전인 Todo 목록을 조회
    List<Todo> findByIsCompletedTrueAndCompletedAtBefore(LocalDateTime cutoffDate);
}
