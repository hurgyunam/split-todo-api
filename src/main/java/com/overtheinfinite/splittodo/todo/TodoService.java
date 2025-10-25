package com.overtheinfinite.splittodo.todo;

import com.overtheinfinite.splittodo.todo.domain.ArchivedTodo;
import com.overtheinfinite.splittodo.todo.domain.Todo;
import com.overtheinfinite.splittodo.todo.domain.User;
import com.overtheinfinite.splittodo.todo.dto.TodoCreateRequest;
import com.overtheinfinite.splittodo.todo.dto.TodoResponse;
import com.overtheinfinite.splittodo.todo.repository.ArchivedTodoRepository;
import com.overtheinfinite.splittodo.todo.repository.TodoRepository;
import com.overtheinfinite.splittodo.auth.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final ArchivedTodoRepository archivedTodoRepository;

    // 📌 Helper Method: 소유권 체크
    // Todo 엔티티와 현재 인증된 사용자 ID를 비교하여 소유자가 다르면 예외를 던집니다.
    private void checkTodoOwnership(Todo todo, Long currentUserId) {
        // 연관 엔티티인 User 객체의 ID와 currentUserId를 비교
        if (!Objects.equals(todo.getUser().getId(), currentUserId)) {
            // 실제 프로젝트에서는 AccessDeniedException 또는 Custom Forbidden Exception을 사용합니다.
            throw new RuntimeException("Access Denied: Todo does not belong to the authenticated user.");
        }
    }
    // --- 1. 할 일 목록 조회 (GET) ---
    @Transactional(readOnly = true)
    // 요청된 userId와 현재 인증된 currentUserId가 일치해야 목록을 볼 수 있도록 강제
    public List<TodoResponse> getTodosByUserId(Long requestedUserId, Long currentUserId) {
        // 📌 본인 확인 체크: 다른 사용자의 목록을 조회하려는 시도 차단
        if (!Objects.equals(requestedUserId, currentUserId)) {
            throw new RuntimeException("Access Denied: Cannot view other users' todo lists.");
        }

        List<Todo> todos = todoRepository.findByUser_Id(requestedUserId);

        return todos.stream()
                .map(TodoResponse::new)
                .collect(Collectors.toList());
    }

    // --- 2. 할 일 생성 (POST) ---
    @Transactional
    public Todo createTodo(TodoCreateRequest todoCreateRequest, Long currentUserId) {
        // 📌 본인 확인 체크: 요청 DTO의 userId가 현재 인증된 사용자와 일치해야 생성 가능
        if (!Objects.equals(todoCreateRequest.getUserId(), currentUserId)) {
            throw new RuntimeException("Access Denied: Cannot create a todo for another user.");
        }

        User userReference = userRepository.getReferenceById(todoCreateRequest.getUserId());

        Todo parentReference = null;
        if (todoCreateRequest.getParentId() != null) {
            // NOTE: 부모 Todo가 있다면, 부모 Todo 역시 본인의 소유인지 확인하는 로직이 추가될 수 있습니다.
            parentReference = todoRepository.getReferenceById(todoCreateRequest.getParentId());
        }

        Todo newTodo = new Todo(todoCreateRequest.getTitle(), parentReference);
        newTodo.setUser(userReference);

        return todoRepository.save(newTodo);
    }

    // --- 3. 핵심 내용 및 계층 구조 변경 (PATCH Details) ---
    @Transactional
    public Todo updateTodoDetails(Long todoId, String newTitle, Long currentUserId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found with id: " + todoId));

        // 📌 본인 확인 체크 로직 호출
        checkTodoOwnership(todo, currentUserId);

        if (newTitle != null && !newTitle.isBlank()) {
            todo.setTitle(newTitle);
        }

        return todo;
    }
    @Transactional
    public Todo updateTodoParent(Long todoId, Long newParentId, Long currentUserId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found with id: " + todoId));

        // 📌 본인 확인 체크 로직 호출
        checkTodoOwnership(todo, currentUserId);

        if (newParentId != null) {
            Todo parentReference = todoRepository.getReferenceById(newParentId);
            // 부모 Todo에 대한 소유권 체크도 추가할 수 있습니다.
            // checkTodoOwnership(parentReference, currentUserId);
            todo.setParent(parentReference);
        }

        return todo;
    }

    // --- 4. 완료 상태 변경 (PATCH Completion) ---
    @Transactional
    public Todo updateIsCompleted(Long todoId, boolean isCompleted, Long currentUserId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found with id: " + todoId));

        // 본인 확인 체크 로직 호출
        checkTodoOwnership(todo, currentUserId);

        // 📌 핵심 로직: 완료 상태 변경 시 completedAt 업데이트
        if (todo.isCompleted() != isCompleted) { // 상태가 실제로 변경될 때만 처리
            todo.setCompleted(isCompleted);

            if (isCompleted) {
                // 완료됨: 현재 시각 기록
                todo.setCompletedAt(LocalDateTime.now());
            } else {
                // 완료 취소: 시간 초기화
                todo.setCompletedAt(null);
            }
        }

        return todo;
    }

    @Transactional
    public void archiveOldTodos() {
        // 1. 아카이브 기준 시점 계산: 현재 시간 - 1달
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);

        // 2. 아카이브 대상 조회 (isCompleted=true 이고 completedAt < cutoffDate)
        List<Todo> todosToArchive = todoRepository.findByIsCompletedTrueAndCompletedAtBefore(cutoffDate);

        if (todosToArchive.isEmpty()) {
            return;
        }

        // 3. 아카이브 테이블에 저장 (INSERT)
        List<ArchivedTodo> archivedTodos = todosToArchive.stream()
                .map(ArchivedTodo::new) // Todo 엔티티를 ArchivedTodo DTO/엔티티로 변환
                .collect(Collectors.toList());

        archivedTodoRepository.saveAll(archivedTodos);

        // 4. 원본 Todo 테이블에서 삭제 (DELETE)
        todoRepository.deleteAll(todosToArchive);
    }
}
