package kr.co.newgyo.article.service;

import jakarta.transaction.Transactional;
import kr.co.newgyo.article.dto.ArticleListResponse;
import kr.co.newgyo.article.dto.ArticleResponse;
import kr.co.newgyo.article.dto.SummaryRequest;
import kr.co.newgyo.article.dto.SummaryResponse;
import kr.co.newgyo.article.entity.Article;
import kr.co.newgyo.article.entity.Summary;
import kr.co.newgyo.article.repository.ArticleQueryRepository;
import kr.co.newgyo.article.repository.ArticleRepository;
import kr.co.newgyo.article.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 스케줄링 및 크롤링, 요약 데이터 저장 로직
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository repository;
    private final SummaryRepository summaryRepository;
    private final ArticleQueryRepository queryRepository;

    private final ArticleCrawlerService articleCrawlerService;
    private final ArticleSummaryService articleSummaryService;

    @Scheduled(fixedDelay = 60 * 60 * 1000) // 1시간
    public void scheduledCrawler() {
        // 파이썬 서버 헬스 체크
        if (!articleCrawlerService.isHealth()) {
            log.warn("[파이썬 서버 안 떠 있음 - 스킵]");
            return;
        }

        // 크롤링
//        crawler();

        // 요약
//        summary();
    }

    public void crawler(){
        try {
            // 크롤링 데이터 목록 수신
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

            createArticles(response);

        } catch (Exception e) {
            log.error("[크롤링 오류]", e);
        }
    }

    public void summary(){
        try {
            // ready 데이터(id, content) 꺼내오기
            List<SummaryRequest> requests = queryRepository.findSummary();

            // getSummary에 보내기
            List<SummaryResponse> responses = articleSummaryService.getSummary(requests);

            // 결과 반환 후 summary 테이블에 저장
            createSummaries(responses);
        } catch (Exception e) {
            log.error("[요약 오류]", e);
        }
    }

    @Transactional
    public void createArticles(List<ArticleResponse> response) {
        List<Article> articles = new ArrayList<>();
        // 크롤링 데이터 디비 저장
        for (ArticleResponse data : response) {
            // 중복 기사 제외
            if (repository.existsByUrl(data.getUrl())) {
                continue;
            }

            Article article = Article.builder()
                    .title(data.getTitle())
                    .content(data.getContent())
                    .language("Korea")
                    .reporter(String.valueOf(data.getReporters()))
                    .url(data.getUrl())
                    .articleDate(data.getDate())
                    .build();

            articles.add(article);
        }

        if(!articles.isEmpty()){
            repository.saveAll(articles);
            log.info("[뉴스 저장 완료] {} 건", articles.size());
        }
    }

    @Transactional
    public void createSummaries(List<SummaryResponse> responses){
        List<Summary> summaries = new ArrayList<>();
        // ai 요약 데이터 디비 저장
        for(SummaryResponse data : responses){
            // article 찾기
            Article article = repository.findById(data.id()).orElseThrow(() -> new IllegalArgumentException("기사 없음"));

            // 뉴스 요약
            Summary summary = Summary.builder()
                    .article(article)
                    .summary(data.summary())
                    .build();

            summaries.add(summary);

            // 요약 상태 변경
            article.updateSummaryStatus();
            repository.save(article);
        }

        if(!summaries.isEmpty()){
            summaryRepository.saveAll(summaries);
            log.info("[뉴스 요약 완료] {} 건", summaries.size());
        }
    }
}
