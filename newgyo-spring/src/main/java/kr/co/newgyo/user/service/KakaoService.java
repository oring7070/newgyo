package kr.co.newgyo.user.service;

import kr.co.newgyo.jwt.JwtUtil;
import kr.co.newgyo.user.dto.KakaoTokenResponse;
import kr.co.newgyo.user.dto.KakaoUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class KakaoService {
    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.user-uri}")
    private String userUri;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final WebClient webClient;

    private final JwtUtil  jwtUtil;

    public KakaoService(WebClient.Builder webClientBuilder, JwtUtil jwtUtil) {
        this.webClient = webClientBuilder.build();
        this.jwtUtil = jwtUtil;
    }

    // TODO : api로 분리
    public String getAccessTokenFromKakao(String code){
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);

        KakaoTokenResponse response = webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();

        log.info("[Access Token] {}", response.getAccessToken());
        log.info("[Refresh Token] {}", response.getRefreshToken());
        log.info("[Id Toke] {}", response.getIdToken());

        return response.getAccessToken();
    }

    public KakaoUserInfo getUserInfo(String accessToken){
        KakaoUserInfo response = webClient.get()
                .uri(userUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfo.class)
                .block();

        log.info("[User Id] {}", response.getId());
        log.info("[User Email] {}", response.getEmail());
        log.info("[User Nickname] {}", response.getNickname());

        return response;
    }

    public String creatJWTtoken(KakaoUserInfo response){
        // 우리서버에 이메일과 같은지 비교후 jwt토큰 발행하는 매서드 생성

        // jwt토큰 생성
        return jwtUtil.createToken(response.getEmail(),"ADMIN");
    }
}
