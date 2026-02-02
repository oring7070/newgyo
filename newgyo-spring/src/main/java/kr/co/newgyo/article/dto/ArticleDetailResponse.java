package kr.co.newgyo.article.dto;

import kr.co.newgyo.article.enums.SummaryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetailResponse extends ArticleResponse{
    SummaryStatus summaryStatus;
    String summary;

    public ArticleDetailResponse(ArticleResponse response, SummaryStatus summaryStatus, String summary) {
        super(
            response.getId(),
            response.getTitle(),
            response.getCategory(),
            response.getImages(),
            response.getReporter(),
            response.getContent(),
            response.getDate(),
            response.getUrl()
        );

        this.summaryStatus = summaryStatus;
        this.summary = summary;
    }
}
