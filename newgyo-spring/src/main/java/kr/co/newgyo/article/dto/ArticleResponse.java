package kr.co.newgyo.article.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ArticleResponse {
    String title;
    List<String> images;
    List<String> reporters;
    String content;
    String date;
    String url;
}
