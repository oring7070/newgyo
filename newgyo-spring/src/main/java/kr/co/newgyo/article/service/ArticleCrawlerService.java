package kr.co.newgyo.article.service;

import kr.co.newgyo.article.dto.ArticleListResponse;
import kr.co.newgyo.article.dto.HealthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 파이썬 서버로 크롤링 요청
 * */
@Slf4j
@Service
public class ArticleCrawlerService {
    private final WebClient webClient;

    public ArticleCrawlerService(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8000")
                .build();
    }

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

    public boolean isHealth() {
        try {
            HealthResponse response = webClient.get()
                    .uri("/api/health")
                    .retrieve()
                    .bodyToMono(HealthResponse.class)
                    .block();

            return response != null && "up".equals(response.getStatus());

        } catch (Exception e) {
            log.error("[파이썬 서버 헬스체크 오류]", e);
            return false;
        }
    }
}