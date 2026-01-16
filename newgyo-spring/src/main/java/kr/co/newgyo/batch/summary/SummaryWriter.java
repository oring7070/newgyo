package kr.co.newgyo.batch.summary;

import kr.co.newgyo.article.entity.Article;
import kr.co.newgyo.article.entity.Summary;
import kr.co.newgyo.article.repository.ArticleRepository;
import kr.co.newgyo.article.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 요약 결과 DB 저장
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class SummaryWriter implements ItemWriter<SummaryResult> {
    private final ArticleRepository articleRepository;
    private final SummaryRepository summaryRepository;

    @Override
    public void write(Chunk<? extends SummaryResult> chunk) throws Exception{
        List<Article> articles = new ArrayList<>();
        List<Summary> summaries = new ArrayList<>();

        // chunk 내 처리 결과 분리
        for(SummaryResult data : chunk){
            if(data != null){
                articles.add(data.getArticle());
                summaries.add(data.getSummary());
            }
        }

        // 기사가 있을 경우 DB 저장
        if(!articles.isEmpty()){
            articleRepository.saveAll(articles);
            summaryRepository.saveAll(summaries);
            log.info("[Batch] 요약 저장된 기사 수 {} ", articles.size());
        }
    }
}
