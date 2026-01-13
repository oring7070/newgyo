package kr.co.newgyo.batch;

import kr.co.newgyo.article.entity.Article;
import kr.co.newgyo.article.entity.Summary;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SummaryResult {
    private Article article;
    private Summary summary;
}
