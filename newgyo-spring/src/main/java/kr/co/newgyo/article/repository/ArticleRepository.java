package kr.co.newgyo.article.repository;

import kr.co.newgyo.article.entity.Article;
import kr.co.newgyo.article.enums.SummaryStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Boolean existsByUrl(String url);
    Optional<Article> findById(Long id);
    List<Article> findBySummaryStatus(SummaryStatus summaryStatus);

    Optional<Article> findTopByOrderByIdDesc();
    List<Article> findTop3ByOrderByViewCountDesc();
    List<Article> findTop3ByOrderByIdDesc(Sort sorting);
    List<Article> findTop3ByKeywordIdOrderByIdDesc(Long keywordId, Sort sorting);

//    List<Article> findTop3(Sort sorting);
//    List<Article> findTop3ByKeywordId(Long keywordId, Sort sorting);

    List<Article> findByKeywordId(Long categoryId);
}
