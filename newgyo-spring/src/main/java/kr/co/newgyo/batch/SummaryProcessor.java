package kr.co.newgyo.batch;

import kr.co.newgyo.client.PythonApiClient;
import kr.co.newgyo.article.dto.SummaryRequest;
import kr.co.newgyo.article.dto.SummaryResponse;
import kr.co.newgyo.article.entity.Article;
import kr.co.newgyo.article.entity.Summary;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AI 요약 서버 호출
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class SummaryProcessor implements ItemProcessor<Article, SummaryResult> {
    private final PythonApiClient pythonApiClient;

    @Override
    public SummaryResult process(@NonNull Article article){
        try {
            // 요약할 기사 추출
            SummaryRequest request = new SummaryRequest(
                    article.getId(),
                    article.getContent()
            );

            // AI 요약 서버 호출
            List<SummaryResponse> responses = pythonApiClient.getSummary(List.of(request));

            if (responses.isEmpty()) {
                throw new IllegalStateException("요약 결과가 없습니다.");
            }

            SummaryResponse response = responses.getFirst();

            // 기사 상태 변경
            article.updateSummaryStatus();

            // 요약 엔티티 생성
            Summary summary = Summary.builder()
                    .article(article)
                    .summary(response.summary())
                    .build();

            // article, summary를 writer로 전달
            return new SummaryResult(article, summary);

        }catch (Exception e) {
            log.error("[Processing 실패] {}", article.getId(), e);
            return null;
        }
    }
}
