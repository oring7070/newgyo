package kr.co.newgyo.article.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListResponse {
    @JsonProperty("data")
    List<ArticleResponse> articleListResponse;
    int count;
}
