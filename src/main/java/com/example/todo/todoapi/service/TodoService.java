package com.example.todo.todoapi.service;

import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.request.TodoModifyRequestDTO;
import com.example.todo.todoapi.dto.response.TodoDetailResponseDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.entity.Todo;
import com.example.todo.todoapi.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoListResponseDTO create(TodoCreateRequestDTO requestDTO) throws Exception {
        todoRepository.save(requestDTO.toEntity());//dto->entity
        log.info("할 일 저장 완료 {}", requestDTO.getTitle());
        return retrieve();
    }

    //할 일 목록 가져오기 //@@@복습 List<Todo> 역할
    public TodoListResponseDTO retrieve() throws Exception {
        List<Todo> entityList = todoRepository.findAll();

        List<TodoDetailResponseDTO> dtoList = entityList.stream()
                //.map(entity -> new TodoDetailResponseDTO(entity))//람다
                .map(TodoDetailResponseDTO::new)
                .collect(Collectors.toList());

        return TodoListResponseDTO.builder()
                .todos(dtoList)
                .build();

    }

    public TodoListResponseDTO delete(final String todoId) throws Exception { //+final todoId 변경 불가

        Todo todo = todoRepository.findById(todoId).orElseThrow(
                () -> {
                    log.error("id가 존재하지 않아 삭제에 실패했습니다 -ID: {}", todoId);
                    throw new RuntimeException("id가 존재하지 않아 삭제에 실패했습니다.");
                }
        );

        todoRepository.deleteById(todoId);

        return retrieve();
    }

    public TodoListResponseDTO update(final TodoModifyRequestDTO requestDTO) throws Exception {
        //jpql 업데이트문 or JPA
        //JPA 방식
        Optional<Todo> targetEntity = todoRepository.findById(requestDTO.getId());
        //Optional은 NPE 발생 방지

        targetEntity.ifPresent(todo -> {//만약 존재한다면 메서드( Optional 객체 안에 값이 있는 경우에만)
            todo.setDone(requestDTO.isDone());
            todoRepository.save(todo);
        });
        return retrieve(); //왜 리턴타입 변경되는지
    }
}
