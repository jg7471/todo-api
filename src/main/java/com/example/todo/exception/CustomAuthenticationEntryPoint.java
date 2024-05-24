package com.example.todo.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component //빈등록
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override //HttpServletRequest request 401 발생할 때만@@@
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //AuthenticationException(인증 예외)

        /*
        AuthenticationEntryPoint 인터페이스를 구현한 클래스를 제작하고, WebSecurityConfig에 등록하면
        인증 과정에서 발생하는 예외를 일괄적으로 처리할 수 있습니다.
        401 상태(인증 과정, 로그인 안 됐거나, 토큰에 문제 있는 경우)에 관련된 예외들만 반응.
        만약 인증과 꼭 관련된 것이 아닌 다른 예외도 세밀하게 처리해야 한다면 Exception 전용 필터를 구축하는 것이
        더 좋음
        * */


        log.warn("AuthenticationEntryPoint에서 commence가 호출됨!");
        //ErrorCode errorCode = (ErrorCode) request.getAttribute("exception");

        //switch (errorCode.getHttpStatus().toString()) { //toString 문자열로 비교

        }
    }

