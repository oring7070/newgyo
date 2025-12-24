package kr.co.newgyo.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분
    public static final String BEARER_PREFIX = "Bearer ";


    //생성자 주입으로 JWT 시크릿 키를 안전하게 초기화하여 불변성을 보장
    @PostConstruct
    public void init() {
        byte[] byteSecretKey = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(byteSecretKey);
    }

    public String createToken(String username, String role) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID)
                        .claim("role", role) // 사용자 권한
                        .setExpiration(new Date(System.currentTimeMillis() + TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, SignatureAlgorithm.HS256) // 암호화 알고리즘
                        .compact();

    }

    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parser()  // 최신 방식 추천
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();  // ← subject에서 추출 (setSubject(username)으로 넣었으니까!)
        } catch (Exception e) {
            System.out.println("토큰에서 username 추출 실패: " + e.getMessage());
            return null;
        }
    }

    public Boolean isExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .setSigningKey(key)  // ← key는 SecretKey 타입
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            return expiration.before(new Date());
        } catch (Exception e) {
            // 만료되거나 유효하지 않은 토큰은 true로 처리 (또는 필요에 따라 false)
            return true;
        }
    }

    //JWTFilter에서 사용할 validateToken 메서드 추가
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            // 1. 토큰에서 username 추출
            String username = getUsernameFromToken(token);

            // 2. 토큰이 만료되지 않았는지 확인
            boolean notExpired = !isExpired(token);

            // 3. 추출한 username과 UserDetails의 username이 일치하는지 확인
            boolean usernameMatches = username != null && username.equals(userDetails.getUsername());

            return usernameMatches && notExpired;
        } catch (Exception e) {
            // 서명 위변조, 형식 오류 등 모든 예외 발생 시 false
            return false;
        }
    }
}
