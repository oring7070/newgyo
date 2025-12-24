package kr.co.newgyo.join.conttoller;

import kr.co.newgyo.join.service.JoinService;
import kr.co.newgyo.user.dto.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JoinController {
    private final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    //회원가입 화면
    @GetMapping("/join-page")
    public String UserLogin(){
        return "join";
    }

    //회원가입 POST
    @PostMapping("/join")
    public String join(@RequestBody UserDto userDto) {
        joinService.joinProcess(userDto);

        return "redirect:/login-page";
    }
    //클라이언트에서 fetch를 사용할경우 return "redirect:/login-page"은 동작하지 않는다
    //json 데이터로 리다이렉트와 주소를 보내 클라이언트에서 이동시킨다

}
