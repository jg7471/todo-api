package com.example.todo.auth;

import com.example.todo.userapi.entity.Role;
import lombok.*;

import java.util.Objects;


@Getter @ToString
@EqualsAndHashCode //객체 동등한지 비교 : a, b 객체 @@ 없어도 그만
@NoArgsConstructor
@AllArgsConstructor
@Builder //builder 쓰려면 noArg 필수?? @@
public class TokenUserInfo {
    private String userId; //사용자 식별용 pk : id아님
    private String email;
    private Role role;

}
