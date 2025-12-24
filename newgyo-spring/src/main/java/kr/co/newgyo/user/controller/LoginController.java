package kr.co.newgyo.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {
    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @GetMapping
    public String loginPage(Model model){
        String url = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+clientId+"&redirect_uri="+redirectUri;
        model.addAttribute("kakao", url);

        return "login";
    }
}