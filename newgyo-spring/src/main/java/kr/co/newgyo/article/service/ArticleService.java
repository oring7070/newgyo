package kr.co.newgyo.article.service;

import kr.co.newgyo.article.dto.ArticleDetailResponse;
import kr.co.newgyo.article.dto.ArticleListResponse;
import kr.co.newgyo.article.dto.ArticleResponse;
import kr.co.newgyo.article.entity.Article;
import kr.co.newgyo.article.entity.Summary;
import kr.co.newgyo.article.repository.ArticleRepository;
import kr.co.newgyo.article.repository.SummaryRepository;
import kr.co.newgyo.home.dto.HomeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository repository;
    private final SummaryRepository summaryRepository;

    public HomeResponse home(Long categoryId){
        // 주요 뉴스
        Article main = repository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new IllegalArgumentException("기사 없음"));

        // 인기 뉴스 3개
        List<Article> hot = repository.findTop3ByOrderByViewCountDesc();

        // 각 카테고리 별 3개
        List<Article> category = (categoryId == null)
                ? repository.findTop3ByOrderByIdDesc()
                : repository.findTop3ByKeywordIdOrderByIdDesc(categoryId);

        // Article -> ArticleResponse 변환
        ArticleResponse mainNews = toResponse(main);

        List<ArticleResponse> hotNews = new ArrayList<>();
        for(Article article : hot){
            hotNews.add(toResponse(article));
        }

        List<ArticleResponse> categoryNews = new ArrayList<>();
        for (Article article : category) {
            categoryNews.add(toResponse(article));
        }

        return new HomeResponse(mainNews, hotNews, categoryNews);
    }

    public ArticleListResponse findDomesticArticles(Long categoryId){
        List<ArticleResponse> categoryList = new ArrayList<>();

        List<Article> category = (categoryId == null)
                ? repository.findAll()
                : repository.findByKeywordId(categoryId);

        for(Article article : category){
            categoryList.add(toResponse(article));
        }

        return new ArticleListResponse(categoryList, category.size());
    }

    public ArticleDetailResponse findArticleById(Long id){
        // 기사 찾기
        Article article = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("기사 없음"));

        // 요약 찾기
        Summary summary = summaryRepository.findByArticleId(article.getId())
                .orElse(null);

        // 요약 없을 경우
        String summaryText = summary != null ? summary.getSummary() : "";

        return new ArticleDetailResponse(toResponse(article), article.getSummaryStatus(), summaryText);
    }

    private ArticleResponse toResponse(Article article) {
        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getKeyword().getName(),
                List.of(),
                article.getReporter(),
                article.getContent(),
                article.getArticleDate(),
                article.getUrl()
        );
    }
}
