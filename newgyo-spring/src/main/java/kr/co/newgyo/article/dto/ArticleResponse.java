package kr.co.newgyo.article.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ArticleResponse {
    String title;
    String category;
    List<String> images;
    String reporter;
    String content;
    String date;
    String url;
}
