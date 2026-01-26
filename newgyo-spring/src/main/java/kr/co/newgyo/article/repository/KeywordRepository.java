package kr.co.newgyo.article.repository;

import kr.co.newgyo.article.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword,Integer> {
    // Keyword findByKeyword(String keyword); 말고 in 을 사용
    List<Keyword> findByKeywordIn(List<String> keywords);

}
