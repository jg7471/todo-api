package com.example.todo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

//@@
@AllArgsConstructor//모든 필드(httpStatus, message)를 파라미터로 받는 생성자를 자동으로 생성
@Getter//각 필드에 대한 getter 메서드를 자동으로 생성
public enum ErrorCode {

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_AUTH(HttpStatus.UNAUTHORIZED, "검증되지 않은 사용자 입니다."),
    FORBIDDEN_AUTH(HttpStatus.FORBIDDEN, "권한이 없는 사용자 입니다.");

    private final HttpStatus httpStatus; //응답상태 코드로 관리
    private final String message;
}
