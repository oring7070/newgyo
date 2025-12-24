package kr.co.newgyo.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoTokenResponse {
    @JsonProperty("token_type")
    String tokenType;

    @JsonProperty("access_token")
    String accessToken;

    @JsonProperty("id_token")
    String idToken;

    @JsonProperty("expires_in")
    String expiresIn;

    @JsonProperty("refresh_token")
    String refreshToken;

    @JsonProperty("refresh_token_expires_in")
    String refreshTokenExpiresIn;
}
