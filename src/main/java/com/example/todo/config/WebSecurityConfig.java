package com.example.todo.config;


import com.example.todo.exception.CustomAuthenticationEntryPoint;
import com.example.todo.filter.JwtAuthFilter;
import com.example.todo.filter.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 자동 권한 검사를 컨트롤러의 메서드에서 전역적으로 수행하기 위한 설정. //부트3
//@EnableGlobalMethodSecurity(prePostEnabled = true) //부트2 사용x
@RequiredArgsConstructor //JwtAuthFilter 주입 받기
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final CustomAuthenticationEntryPoint entryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    // 시큐리티 기본 설정 (권한처리, 초기 로그인 화면 없애기 ....)
    @Bean // 라이브러리 클래스 같은 내가 만들지 않은 객체를 등록해서 주입받기 위한 아노테이션.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrfConfig -> csrfConfig.disable()) // CSRF 토큰공격을 방지하기 위한 장치 해제.
                .cors(Customizer.withDefaults())

                // 세션 관리 상태를 STATELESS로 설정해서 spring security가 제공하는 세션 생성 및 관리 기능 사용하지 않겠다(리액트 session 못씀 : jwt)
                .sessionManagement(httpSecuritySessionManagementConfigurer //변수명 임의로 줄여도 됨
                        -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //spring에서 제공하는 기본 로그인 폼 사용 안함, http 기반 기본인증도 안 쓰겠다.
                .formLogin(form -> form.disable())//1번 람다
                .httpBasic(AbstractHttpConfigurer::disable) //2번 요약

                //우리가 만든 jwtAuthFilter를 UsernamePasswordAuthenticationFilter보다 먼저 동작하게 설정
                //security를 사용하면, 서버가 가동될 때 기본적으로 제공하는 여러가지 필터가 세팅이 되는데,
                //jwtAuthFilter를 먼저 배치해서, 얘를 통과하면 인증이 완료가 되도록 처리

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                //ExceptionFilter 를 AuthFilter 앞에 배치하겠다는 뜻
                //예외 처리만을 전담하는 필터를 생성해서, 예외가 발생하는 필터 앞단에 배치하면,
                //발생된 예외가 먼저 배치된 필터로 넘어가서 처리가 가능하게 됨.
                .addFilterBefore(jwtExceptionFilter, JwtAuthFilter.class) //JwtAuthFilter.class 이 클래스 담당
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests

                                // '/api/todos' 라는 요청이 post로 들어오고, Role 값이 ADMIN인 경우 권한 검사 없이 허용하겠다.
                                //.requestMatchers(HttpMethod.POST, "/api/todos").hasRole("ADMIN")

                                // /api/auth/**은 permit이지만, /promote는 검증이 필요하기에 추가(순서 주의 *permitAll 후순위)
                                .requestMatchers(HttpMethod.PUT, "/api/auth/promote").hasAnyRole("COMMON", "ADMIN")//2개만 예외처리
                                //.hasRole("COMMON") //하나만 체크 : 시큐리티에서 put 방식 권한 설정
                                .requestMatchers(HttpMethod.PUT, "/api/auth/promote").authenticated() //토큰 인증
                                .requestMatchers("/api/auth/load-profile").authenticated()

                                //'/api/auth'로 시작하는 요청과 '/'요청은 권한 검사 없이 허용하겠다.
                                .requestMatchers("/", "/api/auth/**") //마지막에만 .permitAll()
                                .permitAll()
                                // 위에서 따로 설정하지 않은 나머지 요청들은 권한 검사가 필요하다.
                                .anyRequest().authenticated()
                )


                .exceptionHandling(ExceptionHandling -> {
                    //등록
                    // 인증 과정에서 예외가 발생할 경우 예외를 전달한다.(401)
                    //ExceptionHandling.authenticationEntryPoint(entryPoint); //-> jwtAuthFilter 사용이 낫다
                    //인가 과정에서 예외가 발생한 경우 에외를 전달한다.(403)
                    ExceptionHandling.accessDeniedHandler(accessDeniedHandler);
                });

//                .authenticationEntryPoint()
//                .accessDeniedHandler();

        return http.build();
    }

    // 비밀번호 암호화 객체를 빈 등록
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }


}
