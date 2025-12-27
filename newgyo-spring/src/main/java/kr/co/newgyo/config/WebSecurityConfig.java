package kr.co.newgyo.config;

import kr.co.newgyo.jwt.JwtUtil;
import kr.co.newgyo.security.CustomUserDetailsService;
import kr.co.newgyo.security.JWTFilter;
import kr.co.newgyo.security.LoginFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


//이 클래스는 Bean들을 정의하는 설정 클래스
@Configuration
//커스텀 보안 설정을 하려면 반드시 @Configuration 클래스에 @EnableWebSecurity를 붙여야 합니다.
@EnableWebSecurity
public class WebSecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public WebSecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtUtil jwtUtil,CustomUserDetailsService customUserDetailsService) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    // 비밀번호 해싱 bean 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    //loginFilter bean 등록
    @Bean
    public LoginFilter loginFilter()throws Exception {
        LoginFilter filter = new LoginFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf((auth) -> auth.disable());

//        토큰 인증이면 formLogin / httpBasic 끄는 게 맞음
//        안 끄면 redirect, 경로 충돌 생김
        http.formLogin((auth) -> auth.disable());
        http.httpBasic((auth) -> auth.disable());

//          spring security는 세션방식으로 인증/인가를 하는데, jwt는 세션을 사용하지 않고,
//          클라이언트가 매 요청마다 헤더 토큰을 넣어보낸다 서버는 그토큰만 검증하면 되고 서버에 사용자 세션을 저장할
//          필요가 없어 세션을 사용하지 않는다는 설정
        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests(auth -> auth
                        // 🔓 HTML 페이지
                        .requestMatchers(
                                "/home",
                                "/loginPage",
                                "/join-page",
                                "/JS/**",
                                "/css/**",
                                "/login/kakao",
                                "/kakao-callback.html",
                                "/article/domestic",
                                "/article/international"
                        ).permitAll()
                        // 🔐 보호할 API
                        .requestMatchers("/api/**").authenticated().anyRequest().denyAll()
                );


        //JWTFilter 등록
        http.addFilterBefore(new JWTFilter(jwtUtil,customUserDetailsService), LoginFilter.class);
        //필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함)
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }



}
