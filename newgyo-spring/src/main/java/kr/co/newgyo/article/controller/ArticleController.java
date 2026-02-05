package kr.co.newgyo.article.controller;

import kr.co.newgyo.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {
    private final ArticleService service;

    // 국내 뉴스 list
    @GetMapping("/domestic")
    public String domestic(@RequestParam(required = false) Long categoryId, Model model){
        model.addAttribute("domestic", service.getDomesticArticles(categoryId));

        // 카테고리 필터
        model.addAttribute("category", service.getAllCategory());
        model.addAttribute("categoryId", categoryId);

        return "article";
    }

    // 해외 뉴스 list
    @GetMapping("/international")
    public String international(@RequestParam(required = false) Long categoryId, Model model){
        model.addAttribute("international", null);
        return "article";
    }

    // 상세 페이지
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model){
        model.addAttribute("article", service.getArticleById(id));
        return "article-detail";
    }
}