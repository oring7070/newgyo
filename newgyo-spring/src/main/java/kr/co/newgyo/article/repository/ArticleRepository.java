package kr.co.newgyo.article.repository;

import kr.co.newgyo.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Boolean existsByUrl(String url);
}
