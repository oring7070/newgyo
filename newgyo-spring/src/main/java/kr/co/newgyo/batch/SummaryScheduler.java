package kr.co.newgyo.batch;

import kr.co.newgyo.client.HealthCheckClient;
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
 * 기사 요약 배치 실행하는 스케줄러
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class SummaryScheduler {
    private final HealthCheckClient healthCheckClient;

    private final JobLauncher jobLauncher;
    private final Job summaryJob;

    @Scheduled(fixedDelay = 60 * 60 * 1000)  // 1시간
    public void scheduledSummary() {
        if (!healthCheckClient.isHealth()) {
            log.warn("[파이썬 서버 다운 - 스킵]");
            return;
        }

        try {
            // 동일 JobInstance 중복 실행 방지
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            // job 실행
            JobExecution execution = jobLauncher.run(summaryJob, jobParameters);

            log.info("[Batch 실행 결과] {}", execution.getStatus());

        } catch (Exception e) {
            log.error("[Batch 실행 실패]", e);
        }
    }
}