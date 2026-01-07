package kr.co.newgyo.article.service;

import kr.co.newgyo.article.dto.SummaryRequest;
import kr.co.newgyo.article.dto.SummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

/**
 *  파이썬 서버로 요약 요청
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleSummaryService {
    private final WebClient webClient;

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