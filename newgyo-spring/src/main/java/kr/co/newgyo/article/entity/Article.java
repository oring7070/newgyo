package kr.co.newgyo.article.entity;

import jakarta.persistence.*;
import kr.co.newgyo.article.enums.SummaryStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "article")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Keyword keyword;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 50)
    private String language;

    @Column(length = 100)
    private String reporter;

    @Column(length = 500)
    private String url;

    @Enumerated(EnumType.STRING)
    private SummaryStatus summaryStatus;

    private LocalDateTime articleDate;
}
