package kr.co.newgyo.client;

import kr.co.newgyo.article.dto.HealthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 파이썬 서버 헬스체크
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class HealthCheckClient {
    private final WebClient webClient;

    public boolean isHealth() {
        try {
            HealthResponse response = webClient.get()
                    .uri("/api/health")
                    .retrieve()
                    .bodyToMono(HealthResponse.class)
                    .block();

            return response != null && "up".equals(response.getStatus());

        } catch (Exception e) {
            log.warn("[파이썬 서버 헬스체크 오류]", e);
            return false;
        }
    }
}
