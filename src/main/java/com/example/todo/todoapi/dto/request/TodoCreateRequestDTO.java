package com.example.todo.todoapi.dto.request;

import com.example.todo.todoapi.entity.Todo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter @Getter @ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoCreateRequestDTO {

    @NotBlank
    @Size(min = 2, max = 30)
    private String title;

    //dto를 entity로 변환
    public Todo toEntity(){
        return Todo.builder()
                .title(this.title)
                .build();
    }

}