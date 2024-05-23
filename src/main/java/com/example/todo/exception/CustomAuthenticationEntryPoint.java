package com.example.todo.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //AuthenticationException(인증 예외)
        ErrorCode errorCode = (ErrorCode) request.getAttribute("exception");

        switch (errorCode.getHttpStatus().toString()) { //toString 문자열로 비교

        }
    }
}
