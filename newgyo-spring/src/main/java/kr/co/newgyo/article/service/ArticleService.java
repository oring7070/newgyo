package kr.co.newgyo.article.service;

import kr.co.newgyo.article.dto.ArticleListResponse;
import kr.co.newgyo.article.dto.ArticleResponse;
import kr.co.newgyo.article.entity.Article;
import kr.co.newgyo.article.repository.ArticleRepository;
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
    private final ArticleRepository repository;

    private final ArticleCrawlerService articleCrawlerService;

    @Scheduled(fixedDelay = 60 * 60 * 1000) // 1시간
    public void scheduledCrawler(){
        try {
            // 파이썬 서버 헬스 체크
            if(!articleCrawlerService.isHealth()){
                log.warn("[파이썬 서버 오류]");
                return;
            }

            // 크롤링 데이터 목록 수신
            ArticleListResponse listResponse = articleCrawlerService.getCrawler();

            if (listResponse == null || listResponse.getArticleListResponse() == null) {
                log.warn("[크롤링 응답 비었음]");
                return;
            }

            List<ArticleResponse> response = listResponse.getArticleListResponse();

            log.info("[article 수] {}", listResponse.getCount());
            response.forEach(article -> {log.info("[article] {}", article);});

            // 크롤링 데이터 디비 저장
            for(ArticleResponse data : response){
                // 중복 기사 제외
                if(repository.existsByUrl(data.getUrl())){
                    return;
                }

                Article article = Article.builder()
                        .title(data.getTitle())
                        .content(data.getContent())
                        .language("Korea")
                        .reporter(String.valueOf(data.getReporters()))
                        .url(data.getUrl())
                        .articleDate(data.getDate())
                        .build();

                repository.save(article);

            }
        }catch (Exception e){
            log.error("[크롤링 오류]", e);
        }
    }
}
