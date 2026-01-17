package kr.co.newgyo.article.entity;

import jakarta.persistence.*;
import kr.co.newgyo.article.dto.ArticleResponse;
import kr.co.newgyo.article.enums.SummaryStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "article")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 50)
    private String language;

    @Column(length = 100)
    private String reporter;

    @Column(length = 500, nullable = false, unique = true)
    private String url;

    @Enumerated(EnumType.STRING)
    private SummaryStatus summaryStatus = SummaryStatus.READY;

    private String articleDate;

    @Builder
    public Article(String title, Keyword keyword, String content, String language, String reporter, String url, String articleDate){
        this.title = title;
        this.keyword = keyword;
        this.content = content;
        this.language = language;
        this.reporter = reporter;
        this.url = url;
        this.articleDate = articleDate;
    }

    public void updateSummaryStatus(SummaryStatus summaryStatus) {
        this.summaryStatus = summaryStatus;
    }

    public void rollbackSummaryStatus() {
        if(this.summaryStatus == SummaryStatus.PROCESSING){
            this.summaryStatus = SummaryStatus.READY;
        }
    }
}
