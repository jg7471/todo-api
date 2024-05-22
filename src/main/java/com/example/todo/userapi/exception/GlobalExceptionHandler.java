package com.example.todo.userapi.exception;

//전역 예외처리

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
//RestController에서 발생되는 예외를 전역적으로 처리할 수 있게 하는 아노테이션
//예외상황에 따른 응답 방식을 REST 방식으로 클라이언트엑 전달할 수 있다.
//try catch, throws 사용 안해도 됨~!
@RestControllerAdvice
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
