package kr.co.newgyo.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.newgyo.jwt.JwtUtil;
import kr.co.newgyo.user.dto.KakaoTokenResponse;
import kr.co.newgyo.user.dto.KakaoUserInfo;
import kr.co.newgyo.user.entity.Token;
import kr.co.newgyo.user.entity.User;
import kr.co.newgyo.user.enums.LoginProvider;
import kr.co.newgyo.user.enums.Role;
import kr.co.newgyo.user.repository.TokenRepository;
import kr.co.newgyo.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

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

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final TokenCryptoService tokenCryptoService;

    public KakaoService(WebClient.Builder webClientBuilder, JwtUtil jwtUtil, UserRepository userRepository,
                        TokenRepository tokenRepository,TokenCryptoService tokenCryptoService) {
        this.webClient = webClientBuilder.build();
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.tokenCryptoService = tokenCryptoService;

    }

    // 로직 완료해서 컨트롤러에 보내기
    // 어떤작업까지 컨트롤러이고 어떤작업까지 서비스일까
    public String authenticateWithKakao(String code) {
        // 카카오 정보 받기
        KakaoTokenResponse response = getAccessTokenFromKakao(code);

        // 유저 정보 생성
        KakaoUserInfo user = getUserInfo(response.getAccessToken());

        // 카카오 정보 저장 유저 정보 리프레쉬 토큰
        savaKakaoUser(user,response.getAccessToken());

        // 웹 전용 아이디로 회원가입 하면 바뀔게 많을듯..

        // jwt 토큰 생성
        // jwt 토큰만 반환
        return creatJWTtoken(user);
    }

    public KakaoTokenResponse getAccessTokenFromKakao(String code){
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

        return response;
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

    // 카카오 사용자 정보 저장
    public void savaKakaoUser(KakaoUserInfo userInfo, String refreshToken){
        // 카카오 정보 가져와서 저장
        User user  = User.builder()
                .username(userInfo.getEmail())
                .role(Role.USER)
                .isSubscribed(false)
                .provider(LoginProvider.KAKAO)
                .build();

        // 토큰 정보
        // 리프레쉬 토큰 암호화 해서 저장 encryptRefreshToken 사용
        Token token = Token.builder()
                .refreshToken(tokenCryptoService.encryptRefreshToken(refreshToken))
                .build();

        // jpa 영속성 상태 문제로 token 먼저 저장
        // 엔티티 매핑으로 해결가능 CascadeType.ALL
        tokenRepository.save(token);

        user.assignToken(token);

        // 우리서버에 이메일과 같은지 비교후 jwt토큰 발행하는 매서드 생성 (예정)

        userRepository.save(user);

    }

    public String creatJWTtoken(KakaoUserInfo response){
        // jwt토큰 생성
        // 그냥 함수 없애고 위authenticateWithKakao에서 해버리기 (변경)
        return jwtUtil.createToken(response.getEmail(),"ADMIN");
    }


    // 카카오톡 나에게 보내기
    public boolean sendTextToMe(String accessToken, String messageText) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        // 템플릿 객체 (text 타입)
        Map<String, Object> template = new HashMap<>();
        template.put("object_type", "text");
        template.put("text", messageText);
        template.put("link", Map.of(
                "web_url", "https://your-site.com",
                "mobile_web_url", "https://your-site.com"
        ));
        template.put("button_title", "사이트 바로가기");

        try {
            String templateJson = new ObjectMapper().writeValueAsString(template);
            formData.add("template_object", templateJson);

            String response = webClient.post()
                    .uri("https://kapi.kakao.com/v2/api/talk/memo/default/send")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("나에게 메시지 발송 성공: {}", response);
            return true;

        } catch (Exception e) {
            log.error("나에게 메시지 발송 실패: {}", e.getMessage());
            return false;
        }
    }

}
