package kr.co.newgyo.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserContoller {

    //로그인 화면
    @GetMapping("/login-page")
    public String UserLogin(){
        return "login";
    }

}
