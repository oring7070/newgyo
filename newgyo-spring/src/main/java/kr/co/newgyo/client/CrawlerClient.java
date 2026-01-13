package kr.co.newgyo.client;

import kr.co.newgyo.article.dto.ArticleListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 파이썬 서버 크롤링 요청
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlerClient {
    private final WebClient webClient;

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
}
