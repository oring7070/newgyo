package kr.co.newgyo.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
public class JWTTokenResponse {

    private String token;

    public JWTTokenResponse(String token) {
     this.token = token;
    }
}
