package kr.co.newgyo.article.service;

import kr.co.newgyo.article.dto.ArticleListResponse;
import kr.co.newgyo.article.dto.HealthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 파이썬 서버로 크롤링 요청
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleCrawlerService {
    private final WebClient webClient;

    // 파이썬 서버 크롤링 요청
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

    // 파이썬 서버 헬스체크
    public boolean isHealth() {
        try {
            HealthResponse response = webClient.get()
                    .uri("/api/health")
                    .retrieve()
                    .bodyToMono(HealthResponse.class)
                    .onErrorResume(e -> Mono.empty())
                    .block();

            return response != null && "up".equals(response.getStatus());

        } catch (Exception e) {
            log.error("[파이썬 서버 헬스체크 오류]", e);
            return false;
        }
    }
}