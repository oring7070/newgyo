package kr.co.newgyo.batch.summary;

import kr.co.newgyo.article.entity.Article;
import kr.co.newgyo.article.enums.SummaryStatus;
import kr.co.newgyo.article.repository.ArticleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 기사 요약 상태 복구
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class SummaryRecoveryTasklet implements Tasklet {
    private final ArticleRepository repository;

    @Nullable
    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution,
                                @NonNull ChunkContext chunkContext) throws Exception {

        List<Article> processingArticle = repository.findBySummaryStatus(SummaryStatus.PROCESSING);

        for(Article article: processingArticle){
            article.rollbackSummaryStatus();
        }

        return RepeatStatus.FINISHED;
    }
}
