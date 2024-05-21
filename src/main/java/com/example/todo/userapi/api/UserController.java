package com.example.todo.userapi.api;

import com.example.todo.auth.TokenUserInfo;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserSignUpRequestDTO;
import com.example.todo.userapi.dto.response.LoginResponseDTO;
import com.example.todo.userapi.dto.response.UserSignUpResponseDTO;
import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    // 이메일 중복 확인 요청 처리
    // GET: /api/auth/check?email=zzzz@xxx.com
    // jpa는 pk로 조회하는 메서드는 기본 제공되지만, 다른 컬럼으로 조회하는 메서드는 기본 제공되지 않습니다.
    @GetMapping("/check")
    public ResponseEntity<?> check(@RequestParam String email) { //@RequestParam 생략 가능
        //@RequestBody는 HTTP 요청의 본문(body)에서 데이터를 가져와서 매핑할 때 사용
        if (email.trim().isEmpty()) {
            //trim() 공백 제거 //isEmpty() 빈문자열인지 판단
            return ResponseEntity.badRequest()
                    .body("이메일이 없습니다.");
        }

        boolean resultFlag = userService.isDuplicate(email);
        log.info("중복??? - {}", resultFlag);
        return ResponseEntity.ok().body(resultFlag);
    }

    // 회원 가입 요청 처리
    // POST: /api/auth
    @PostMapping
    public ResponseEntity<?> signUp(
            @Validated @RequestBody UserSignUpRequestDTO dto, //JSON 자바로 변환
            BindingResult result
    ) {
        log.info("/api/auth POST! - {}", dto);

        //메서드화 ctrl alt m
        ResponseEntity<FieldError> resultEntity = getFieldErrorResponseEntity(result);
        if (resultEntity != null) return resultEntity;

        try {
            UserSignUpResponseDTO responseDTO = userService.create(dto);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 로그인 요청 처리 메서드를 선언하세요.
    // LoginRequestDTO 클래스를 생성해서 요청 값을 받아 주세요.
    // 서비스로 넘겨서, 로그인 유효성을 검증하세요. (비밀번호 암호화되어 있어요.)
    // 로그인 결과를 응답 상태 코드로 구분해서 보내 주세요.
    // 로그인이 성공했다면 200, 로그인 실패라면 400을 보내주세요 (에러 메세지를 상황에 따라 다르게 전달해 주세요.)
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(
            @Validated @RequestBody LoginRequestDTO dto,
            BindingResult result  //@Validated(검증) 결과
    ) {
        log.info("/api/auth/signin - POST - {}", dto);

        ResponseEntity<FieldError> response = getFieldErrorResponseEntity(result);
        if (response != null) return response;

        try {
            LoginResponseDTO responseDTO = userService.authenticate(dto);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    //일반 회원을 프리미엄 회원으로 승격하는 요청 처리
    @PutMapping("/promote")
    //권한 검사 (해당 권한이 아니라면 인가처리 거부-> 403 상태 리턴)
    //메서드 호출 전에 검사 -> 요청 당시 토큰에 있는 user 정보가 ROLE_COMMON이라는 권한을 가지고 있는지 검사
    @PreAuthorize("hasRole('ROLE_COMMON')")
    public ResponseEntity<?> promote(
            @AuthenticationPrincipal TokenUserInfo userInfo
    ){
        log.info("/api/auth/promote - PUT!");

        return null;
    }




    private static ResponseEntity<FieldError> getFieldErrorResponseEntity(BindingResult result) {
        if (result.hasErrors()) {
            log.warn(result.toString());
            return ResponseEntity.badRequest()
                    .body(result.getFieldError());
        }
        return null;
    }




}


















