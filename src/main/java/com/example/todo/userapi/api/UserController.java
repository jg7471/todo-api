package com.example.todo.userapi.api;


import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    //이메일 중복 확인 요청 처리
    // GET: /api/auth/check?email=zzzz@xxx.com
    // jpa는 pk로 조회하는 메서드는 기본 제공되지만,
    // 다른 컬럼으로 조회하는 메서드는 기본 제공되지 않습니다.

    @GetMapping("/check")
    public ResponseEntity<?> check(@RequestParam String email) { //@RequestParam 생략 가능
        //@RequestBody는 HTTP 요청의 본문(body)에서 데이터를 가져와서 매핑할 때 사용
        if (email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("이메일이 없습니다.");
        }

        boolean resultFlag = userService.isDuplicate(email);
        log.info("중복??? - {}", resultFlag);
        return ResponseEntity.ok().body(resultFlag);
    }

}
