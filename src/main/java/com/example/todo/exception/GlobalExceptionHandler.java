package com.example.todo.exception;

//전역 예외처리

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
//Controller 에서 발생한 에러 처리~!~!

//RestController에서 발생되는 예외를 전역적으로 처리할 수 있게 하는 아노테이션
//예외상황에 따른 응답 방식을 REST 방식으로 클라이언트엑 전달할 수 있다.
//try catch, throws 사용 안해도 됨~!

/*
    @RestControllerAdvice로 등록된 전역 예외 처리 방식은 Controller에서 발생된 예외만 처리합니다.
    Service, Repository, Filter에서 발생하는 예외는 처리하지 못합니다.
    ExpiredJwtException 처럼 security filter 단에서 발생하는 예외 타입은 애초에 요청 자체가
    Controller에 닿지 못하기 때문에 처리할 수 없습니다.

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleRuntimeException(ExpiredJwtException e) {
        log.info("ExpiredJwtException 발생!");
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    */

//@ControllerAdvice //일반 예외상황 응답
public class GlobalExceptionHandler {
    //RuntimeException 오류 발생시 전역적으로 처리함
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e){ //응답 response 방식
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleRuntimeException(IllegalArgumentException e){ //응답 response 방식
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        //HttpStatus. 찍으면 많은 에러 종류 뜸
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleRuntimeException(Exception e){ //응답 response 방식
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
