package kr.co.newgyo.batch.crawler;

import kr.co.newgyo.client.PythonApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 기사 크롤링 배치 실행하는 스케줄러
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlerScheduler {
    private final PythonApiClient pythonApiClient;

    private final JobLauncher jobLauncher;
    private final Job crawlerJob;

//    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void scheduledCrawler() {
        if (!pythonApiClient.isHealth()){
            log.warn("[파이썬 서버 다운 - 스킵]");
            return;
        }

        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(crawlerJob, jobParameters);

            log.info("[Crawl Batch 실행 결과] {}", execution.getStatus());

        } catch (Exception e) {
            log.error("[Crawl Batch 실행 실패]", e);
        }
    }
}
