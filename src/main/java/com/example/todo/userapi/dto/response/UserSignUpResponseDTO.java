package com.example.todo.userapi.dto.response;

import com.example.todo.userapi.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Setter @Getter @ToString
@EqualsAndHashCode(of = "email")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpResponseDTO {


    private String email;

    private String userName;




    //entity -> DTO
    public UserSignUpResponseDTO(User saved) {
        this.email = saved.getEmail();
        this.userName = saved.getUserName();
    }

}

