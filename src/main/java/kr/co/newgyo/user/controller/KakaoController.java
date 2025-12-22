package kr.co.newgyo.user.controller;

import kr.co.newgyo.user.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoController {

    private final KakaoService service;

    @GetMapping("/login/kakao")
    public ResponseEntity<?> kakao(@RequestParam("code") String code) {
        String accessToken = service.getAccessTokenFromKakao(code);
        service.getUserInfo(accessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
