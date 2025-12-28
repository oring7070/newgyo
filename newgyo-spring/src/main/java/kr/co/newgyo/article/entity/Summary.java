package kr.co.newgyo.article.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name="summary")
public class Summary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(length = 500)
    private String summary;
}
