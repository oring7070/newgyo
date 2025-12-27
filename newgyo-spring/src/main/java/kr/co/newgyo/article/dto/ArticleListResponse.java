package kr.co.newgyo.article.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ArticleListResponse {
    @JsonProperty("data")
    List<ArticleResponse> articleListResponse;
    int count;
}
