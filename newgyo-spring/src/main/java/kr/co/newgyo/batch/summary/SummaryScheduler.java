package kr.co.newgyo.batch.summary;

import kr.co.newgyo.client.PythonApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 기사 요약 배치 실행하는 스케줄러
 * */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class SummaryScheduler {
    private final PythonApiClient pythonApiClient;

    private final JobLauncher jobLauncher;
    private final Job summaryJob;

//    @Scheduled(fixedDelay = 70 * 60 * 1000)  // 1시간 10분
    public void scheduledSummary() {
        if (!pythonApiClient.isHealth()) {
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

            log.info("[Summary Batch 실행 결과] {}", execution.getStatus());

        } catch (Exception e) {
            log.error("[Summary Batch 실행 실패]", e);
        }
    }
}