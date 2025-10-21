package com.overtheinfinite.splittodo.repository;

import com.overtheinfinite.splittodo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    // 추가적인 사용자 정의 쿼리가 필요하면 여기에 선언합니다.
}
