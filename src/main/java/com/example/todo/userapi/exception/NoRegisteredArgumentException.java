package com.example.todo.userapi.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoRegisteredArgumentException extends RuntimeException{ //Exception 범용성

    //기본 생성자 + 에러메시지를 받는 생성자
    public NoRegisteredArgumentException(String message) {
        super(message);
    }
}
