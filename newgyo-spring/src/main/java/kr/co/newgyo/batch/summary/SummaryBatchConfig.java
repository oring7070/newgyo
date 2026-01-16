package kr.co.newgyo.batch.summary;

import jakarta.persistence.EntityManagerFactory;
import kr.co.newgyo.article.entity.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * 기사 요약 batch
 * */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SummaryBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final SummaryProcessor summaryProcessor;
    private final SummaryWriter summaryWriter;
    private final SummaryRecoveryTasklet summaryRecoveryTasklet;

    @Bean
    public Job summaryJob() {
        return new JobBuilder("summaryJob", jobRepository)
                .start(summaryRecoveryStep())   // 복구
                .next(summaryStep())       // 후 요약
                .build();
    }

    @Bean
    public Step summaryRecoveryStep() {
        return new StepBuilder("summaryRecoveryStep", jobRepository)
                .tasklet(summaryRecoveryTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step summaryStep() {
        return new StepBuilder("summaryStep", jobRepository)
                .<Article, SummaryResult>chunk(5, transactionManager)
                .reader(summaryReader())
                .processor(summaryProcessor)
                .writer(summaryWriter)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(2) // 2번 실패 시 step에서 제외
                .build();
    }

    @Bean
    @StepScope // Step 실행 시점에 bean 생성
    public JpaPagingItemReader<Article> summaryReader() {
        return new JpaPagingItemReaderBuilder<Article>().name("summaryReader") // job 재시작, 상태 추적에 필수
                .entityManagerFactory(entityManagerFactory).queryString("""
                        SELECT a
                          FROM Article a
                         WHERE a.summaryStatus in ('READY', 'FAILED')
                         ORDER BY a.id
                        """).pageSize(5).build();
    }
}
