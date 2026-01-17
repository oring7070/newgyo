package kr.co.newgyo.batch.crawler;

import jakarta.transaction.Transactional;
import kr.co.newgyo.article.dto.ArticleListResponse;
import kr.co.newgyo.article.dto.ArticleResponse;
import kr.co.newgyo.article.entity.Article;
import kr.co.newgyo.article.entity.Keyword;
import kr.co.newgyo.article.repository.ArticleRepository;
import kr.co.newgyo.article.repository.KeywordRepository;
import kr.co.newgyo.client.PythonApiClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 기사 크롤링을 수행 후 신규 기사만 저장
 * 현재는 파이썬에서 기사를 묶음으로 보내주고 있기 때문에 tasklet으로 진행
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlerTasklet implements Tasklet {
    private final PythonApiClient pythonApiClient;

    private final ArticleRepository articleRepository;
    private final KeywordRepository keywordRepository;

    @Override
    @Transactional
    public RepeatStatus execute(@NonNull StepContribution contribution,
                                @NonNull ChunkContext chunkContext) throws Exception {
        log.info("[Batch] 기사 크롤링 시작");

        // 크롤링 서버 호출
        ArticleListResponse response = pythonApiClient.getCrawler();
        List<ArticleResponse> articleList = response.getArticleListResponse();

        if (articleList.isEmpty()){
            log.info("수집된 기사 없음");
            return RepeatStatus.FINISHED;
        }

        log.info("[Batch] 수집된 기사 수 {}", articleList.size());

        int count = 0;

        // 크롤링 데이터 디비 저장
        for (ArticleResponse data : articleList) {
            // 중복 기사 제외
            if (articleRepository.existsByUrl(data.getUrl())) {
                continue;
            }

            // 키워드 추출
            Keyword keyword = keywordRepository.findByName(data.getCategory().strip())
                    .orElseGet(() -> keywordRepository.findByCode("ETC").orElseThrow());

            Article article = Article.builder()
                    .title(data.getTitle())
                    .keyword(keyword)
                    .content(data.getContent())
                    .language("KOREA")
                    .reporter(data.getReporter())
                    .url(data.getUrl())
                    .articleDate(data.getDate())
                    .build();

            articleRepository.save(article);

            count++;
        }

        log.info("[Batch] 신규 저장 기사 수: {}", count);

        return RepeatStatus.FINISHED;
    }
}
