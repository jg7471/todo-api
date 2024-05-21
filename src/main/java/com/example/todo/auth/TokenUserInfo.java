package com.example.todo.auth;

import com.example.todo.userapi.entity.Role;
import lombok.*;


@Getter @ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder //builder 쓰려면 noArg 필수 @@@
public class TokenUserInfo {
    private String userId; //사용자 식별용 pk : id아님
    private String email;
    private Role role;
}
