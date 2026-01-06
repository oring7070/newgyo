package kr.co.newgyo.article.repository;

import kr.co.newgyo.article.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
}
