package kr.co.newgyo.home.dto;

import kr.co.newgyo.article.dto.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HomeResponse {
    private ArticleResponse mainNews; // 주요 뉴스
    private List<ArticleResponse> hotNews;  // 인기 뉴스
    private List<ArticleResponse> categoryNews; // 카테고리별 뉴스
}