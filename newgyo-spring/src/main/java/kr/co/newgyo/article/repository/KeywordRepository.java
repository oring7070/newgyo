package kr.co.newgyo.article.repository;

import kr.co.newgyo.article.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByName(String name);
    Optional<Keyword> findByCode(String code);
}
