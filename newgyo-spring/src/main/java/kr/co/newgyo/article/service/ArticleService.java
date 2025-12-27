package kr.co.newgyo.article.service;

import kr.co.newgyo.article.dto.ArticleListResponse;
import kr.co.newgyo.article.dto.ArticleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 스케줄링 및 크롤링 데이터 저장 로직
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {
//    private final ArticleRepository repository;

    private final ArticleCrawlerService articleCrawlerService;

    @Scheduled(fixedDelay = 60 * 60 * 1000) // 1시간
    public void scheduledCrawler(){
        try {
            if(!articleCrawlerService.isHealth()){
                log.warn("[파이썬 서버 오류]");
                return;
            }

            ArticleListResponse listResponse = articleCrawlerService.getCrawler();

            if (listResponse == null || listResponse.getArticleListResponse() == null) {
                log.warn("[크롤링 응답 비었음]");
                return;
            }

            List<ArticleResponse> response = listResponse.getArticleListResponse();

            log.info("[article 수] {}", listResponse.getCount());

            response.forEach(article -> {
                log.info("[article] {}", article);
            });
        }catch (Exception e){
            log.error("[크롤링 오류]", e);
        }
    }
}
