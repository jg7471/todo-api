package com.example.todo.filter;

import com.example.todo.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            //예외가 발생하지 않으면 필터를 통과
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) { //ExpiredJwtException 타입의 예외가 발생하면, 해당 예외 객체가 e 변수에 저장
            //토큰이 만료되었을 시 Auth Filter에서 예외가 발생 -> 앞에 있는 Exception Filter로 전달
            request.setAttribute("exception", ErrorCode.EXPIRED_TOKEN);
            setErrorResponse(response, ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException e) {//@@ MalformedJwtException | SignatureException : jwt(부모)=토큰(mal)/서명(sig) : 부모 관계에 따라 위치 상하 조정
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN);
            setErrorResponse(response, ErrorCode.INVALID_TOKEN);
        }
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        //헤더
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json; charset=UTF-8");

        //Map 생성 및 데이터 추가(JSON) //@@ 리액트 응답과정
        Map<String, Object> responseMap = new HashMap<>();
        //JSON 데이터

        responseMap.put("message", errorCode.getMessage());
        responseMap.put("code", errorCode.getHttpStatus());

        //Map을 JSON 문자열로 변환
        String jsonString = new ObjectMapper().writeValueAsString(responseMap);

        //JSON 데이터를 응답객체에 실어서 브라우저로 바로 응답
        response.getWriter().write(jsonString);
   }
}
