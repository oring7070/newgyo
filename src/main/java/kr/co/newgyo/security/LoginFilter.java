package kr.co.newgyo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.newgyo.jwt.JwtUtil;
import kr.co.newgyo.user.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public LoginFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try{
            //json 으로 된 body 데이터 확인 및 데이터로 토큰 만들기
            UserDto UserRequest = new ObjectMapper().readValue(request.getInputStream(), UserDto.class);

            //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
            //jwt토큰이 아니라 security가 생성하는 토큰
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(UserRequest.getUsername(), UserRequest.getPassword(), null);

            //token에 담은 검증을 위한 AuthenticationManager로 전달
            return getAuthenticationManager().authenticate(authToken);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }

    //로그인 성공시
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        //jwt 토큰 생성해서 클라이언트에 저장
        String username = ((CustomUserDetails) authentication.getPrincipal()).getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String token = jwtUtil.createToken(username,role);

        //fetch 를 통해 클라이언트에 토큰 json으로 전달
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        //dto나 map으로 바꾸기
        response.getWriter().write("{\"token\": \"" + token + "\", \"message\": \"로그인 성공!\"}");
    }

    //로그인 실패시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }

}
