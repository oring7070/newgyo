package kr.co.newgyo.client;

import kr.co.newgyo.article.dto.ArticleListResponse;
import kr.co.newgyo.article.dto.HealthResponse;
import kr.co.newgyo.article.dto.SummaryRequest;
import kr.co.newgyo.article.dto.SummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

/**
 * 파이썬 서버와 통신하는 클라이언트
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class PythonApiClient {
    private final WebClient webClient;

    // 헬스체크
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

    // 크롤링 요청
    public ArticleListResponse getCrawler(){
        try {
            ArticleListResponse response = webClient.post()
                    .uri("/api/articles")
                    .retrieve()
                    .bodyToMono(ArticleListResponse.class)
                    .block();

            log.info("[article] {} ", response);
            return response;

        }catch (Exception e){
            log.error("[크롤링 API 오류]", e);
            return null;
        }
    }

    // AI 요약 요청
    public List<SummaryResponse> getSummary(List<SummaryRequest> summary){
        try{
            List<SummaryResponse> response = webClient.post()
                    .uri("/api/summary")
                    .bodyValue(summary)
                    .retrieve()
                    .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals, clientResponse -> {
                        return Mono.error(new RuntimeException("429 Too Many Requests"));
                    })
                    .bodyToFlux(SummaryResponse.class)
                    .delaySubscription(Duration.ofSeconds(1))
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))
                            .filter(throwable -> throwable instanceof RuntimeException &&
                                    throwable.getMessage().contains("429")))
                    .collectList()
                    .block();

            log.info("[article summary] {} ", response);
            return response;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
