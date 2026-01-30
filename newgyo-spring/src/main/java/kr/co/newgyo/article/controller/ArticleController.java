package kr.co.newgyo.article.controller;

import kr.co.newgyo.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/domestic")
    public String domestic(Model model){
        model.addAttribute("domestic",null);
        return "article";
    }

    @GetMapping("/international")
    public String international(Model model){
        model.addAttribute("international", null);
        return "article";
    }

    //상세페이지 디테일 페이지
//    @GetMapping("/detail/{id}")
//    public String detail(@PathVariable Long id, Model model){
//        // 디비에 해당하는 id값의 뉴스가 없을 때
//
//        model.addAttribute("article", articleService.getDetailArticle(id));
//
//        return "articleDetail";
//    }
}