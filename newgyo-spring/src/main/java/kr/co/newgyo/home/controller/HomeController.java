package kr.co.newgyo.home.controller;

import kr.co.newgyo.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final ArticleService articleService;

    @GetMapping
    public String home(@RequestParam(required = false) Long categoryId,
                       Model model){
        model.addAttribute("article", articleService.home(categoryId));
        return "home";
    }
}
