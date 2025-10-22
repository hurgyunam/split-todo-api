package com.overtheinfinite.splittodo.controller;

import com.overtheinfinite.splittodo.domain.Todo;
import com.overtheinfinite.splittodo.dto.*;
import com.overtheinfinite.splittodo.service.TodoService;
import com.overtheinfinite.splittodo.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todo")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    // 📌 시뮬레이션: 세션/인증 컨텍스트에서 현재 로그인한 사용자 ID를 가져옴
    private Long getAuthenticatedUserId() {
        // 실제로는 Spring Security 등을 통해 현재 사용자의 ID를 안전하게 가져오는 로직이 들어갑니다.
        // 예시를 위해 1번 사용자가 로그인했다고 가정하겠습니다.
        return SecurityUtil.getCurrentUserId();
    }

    // --- 1. 할 일 목록 조회 (GET /api/todos?userId={id}) ---
    @GetMapping
    public ResponseEntity<List<TodoResponse>> getTodosForUser(@RequestParam Long userId) {
        Long currentUserId = getAuthenticatedUserId();

        // Service에 요청된 ID와 현재 사용자 ID를 모두 전달
        List<TodoResponse> todoList = todoService.getTodosByUserId(userId, currentUserId);
        return ResponseEntity.ok(todoList);
    }

    // --- 2. 할 일 생성 (POST /api/todos) ---
    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@RequestBody TodoCreateRequest request) {
        Long currentUserId = getAuthenticatedUserId();

        // Service에 DTO와 현재 사용자 ID를 전달
        Todo createdTodo = todoService.createTodo(request, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TodoResponse(createdTodo));
    }

    @PatchMapping("/{todoId}/details")
    public ResponseEntity<TodoResponse> updateTodoDetails(
            @PathVariable Long todoId,
            @RequestBody TodoUpdateDetailsRequest request) {
        Long currentUserId = getAuthenticatedUserId();

        // Service에 모든 수정 정보와 현재 사용자 ID를 전달
        Todo updatedTodo = todoService.updateTodoDetails(
                todoId,
                request.getTitle(),
                currentUserId
        );
        return ResponseEntity.ok(new TodoResponse(updatedTodo));
    }

    @PatchMapping("/{todoId}/parent")
    public ResponseEntity<TodoResponse> updateTodoDetails(
            @PathVariable Long todoId,
            @RequestBody TodoUpdateParentRequest request) {
        Long currentUserId = getAuthenticatedUserId();

        // Service에 모든 수정 정보와 현재 사용자 ID를 전달
        Todo updatedTodo = todoService.updateTodoParent(
                todoId,
                request.getParentId(),
                currentUserId
        );
        return ResponseEntity.ok(new TodoResponse(updatedTodo));
    }

    // --- 4. 완료 상태 변경 (PATCH /api/todos/{todoId}/completion) ---
    @PatchMapping("/{todoId}/completion")
    public ResponseEntity<TodoResponse> updateCompletionStatus(
            @PathVariable Long todoId,
            @RequestBody TodoUpdateCompletionRequest request) {
        Long currentUserId = getAuthenticatedUserId();

        // Service에 완료 상태와 현재 사용자 ID를 전달
        Todo updatedTodo = todoService.updateIsCompleted(
                todoId,
                request.isCompleted(),
                currentUserId
        );
        return ResponseEntity.ok(new TodoResponse(updatedTodo));
    }

    @PostMapping("/cleanup")
    // 권한이 필요할 경우, @PreAuthorize("hasRole('ADMIN')") 등을 사용합니다.
    public ResponseEntity<String> runArchiveCleanup() {
        // 세션에서 사용자 ID를 가져와 권한 확인 로직을 추가할 수 있습니다.

        // TodoArchiveService와 같은 별도의 서비스를 통해 아카이빙 로직을 실행한다고 가정
        // todoArchiveService.archiveOldTodos();

        // 임시로 TodoService에 archiveOldTodos 메서드를 만든다고 가정하고 호출
        todoService.archiveOldTodos();

        return ResponseEntity.ok("Successfully initiated archiving cleanup.");
    }

}
