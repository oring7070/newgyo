package kr.co.newgyo.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfo {
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount{
        private Profile profile;
        private String email;

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Profile {
            private String nickname;
        }
    }

    public String getEmail(){
        if(kakaoAccount != null){
            return kakaoAccount.getEmail();
        }
        return null;
    }

    public String getNickname(){
        if(kakaoAccount != null && kakaoAccount.profile != null){
            return kakaoAccount.getProfile().getNickname();
        }
        return null;
    }
}
