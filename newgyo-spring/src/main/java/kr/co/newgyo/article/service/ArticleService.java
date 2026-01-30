package kr.co.newgyo.article.service;

import kr.co.newgyo.article.dto.ArticleDetailResponse;
import kr.co.newgyo.article.dto.ArticleResponse;
import kr.co.newgyo.article.entity.Article;
import kr.co.newgyo.article.repository.ArticleRepository;
import kr.co.newgyo.home.dto.HomeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository repository;

    public HomeResponse home(Long categoryId){
        // 주요 뉴스
        Article main = repository.findTopByOrderByIdDesc().orElseThrow();

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

    public ArticleDetailResponse getDetailArticle(Long id){
        return null;
    }

}
