package kr.co.newgyo.batch.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 기사 크롤링 batch
 * */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class CrawlerBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CrawlerTasklet crawlerTasklet;

    @Bean
    public Job crawlerJob(){
        return new JobBuilder("crawlerJob", jobRepository)
                .start(crawlerStep())
                .build();
    }

    @Bean
    public Step crawlerStep(){
        return new StepBuilder("crawlerStep", jobRepository)
                .tasklet(crawlerTasklet, transactionManager)
                .build();
    }
}
