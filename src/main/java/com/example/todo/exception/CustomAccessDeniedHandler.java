package com.example.todo.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    //403 에러 관련 처리(인가과정 예외 발생)
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("AccessDeniedHandler가 반응함! handler 메서드 호출!");

        //관리자 권한이 없는 이용자가 관리자 페이지 접근시 유용
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); //403 코드 모르면 이렇게 적어도 됨
        response.setContentType("application/json; charset=UTF=8");

        //Map 생성 및 데이터 추가(JSON) //@@ 리액트 응답과정
        Map<String, Object> responseMap = new HashMap<>();
        //JSON 데이터
        responseMap.put("message", "권한이 없습니다.");
        responseMap.put("code", "403");

        //Map을 JSON 문자열로 변환
        String jsonString = new ObjectMapper().writeValueAsString(responseMap);

        //JSON 데이터를 응답객체에 실어서 브라우저로 바로 응답
        response.getWriter().write(jsonString);

    }
}
