package com.overtheinfinite.splittodo.todo.repository;

import com.overtheinfinite.splittodo.todo.domain.ArchivedTodo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivedTodoRepository extends JpaRepository<ArchivedTodo, Long> {
}
