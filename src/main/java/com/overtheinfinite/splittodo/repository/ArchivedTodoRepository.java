package com.overtheinfinite.splittodo.repository;

import com.overtheinfinite.splittodo.domain.ArchivedTodo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivedTodoRepository extends JpaRepository<ArchivedTodo, Long> {
}
