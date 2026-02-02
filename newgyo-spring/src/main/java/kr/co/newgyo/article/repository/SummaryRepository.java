package kr.co.newgyo.article.repository;

import kr.co.newgyo.article.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
    Optional<Summary> findByArticleId(Long id);
}
