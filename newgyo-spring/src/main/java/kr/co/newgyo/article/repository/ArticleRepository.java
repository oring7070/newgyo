package kr.co.newgyo.article.repository;

import kr.co.newgyo.article.entity.Article;
import kr.co.newgyo.article.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Boolean existsByUrl(String url);

    Optional<Article> findById(Long id);
    // 전체 키워드별 기사
    List<Article> findByKeywordIdIn(Set<Long> keywordIds);

    // 유저별 키워드
    List<Article> findByKeywordId(Long keywordId);
}
