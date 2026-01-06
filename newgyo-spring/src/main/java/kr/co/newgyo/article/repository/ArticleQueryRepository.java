package kr.co.newgyo.article.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.newgyo.article.dto.SummaryRequest;
import kr.co.newgyo.article.enums.SummaryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.newgyo.article.entity.QArticle.article;

@Repository
@RequiredArgsConstructor
public class ArticleQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<SummaryRequest> findSummary(){
        return jpaQueryFactory
                .select(Projections.constructor(
                        SummaryRequest.class,
                        article.id,
                        article.content
                ))
                .from(article)
                .where(article.summaryStatus.in(
                        SummaryStatus.READY,
                        SummaryStatus.FAILED
                ))
                .fetch();
    }
}
