package kr.co.newgyo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.newgyo.jwt.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public JWTFilter(JwtUtil jwtUtil,CustomUserDetailsService customUserDetailsService) {
       this.jwtUtil = jwtUtil;
       this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                // 1. 토큰에서 username 추출 (JwtUtil에 메서드 있어야 함)
                String username = jwtUtil.getUsernameFromToken(token);

                // 2. 이미 인증된 상태가 아니면
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // 3. UserDetails 로드 (CustomUserDetailsService 필요)
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                    // 4. 토큰 유효성 검증
                    if (jwtUtil.validateToken(token, userDetails)) {  // 만료, 서명 등 체크

                        // 5. 인증 객체 생성 및 SecurityContext에 설정 → 이게 핵심!!
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                System.out.println("JWT 검증 실패: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
