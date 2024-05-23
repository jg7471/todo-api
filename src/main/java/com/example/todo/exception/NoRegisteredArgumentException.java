package com.example.todo.exception;

import lombok.NoArgsConstructor;


@NoArgsConstructor
public class NoRegisteredArgumentException extends RuntimeException{ //Exception 범용성

    //기본 생성자 + 에러메시지를 받는 생성자
    public NoRegisteredArgumentException(String message) {
        //부모 클래스인 RuntimeException의 생성자를 호출하여 해당 메시지를 설정
        super(message);
    }
}
