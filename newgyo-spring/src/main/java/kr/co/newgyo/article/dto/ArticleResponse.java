package kr.co.newgyo.article.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponse {
    Long id;
    String title;
    String category;
    List<String> images;
    String reporter;
    String content;
    String date;
    String url;
}
